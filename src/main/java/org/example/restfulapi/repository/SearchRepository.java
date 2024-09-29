package org.example.restfulapi.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.example.restfulapi.dto.response.PageResponse;
import org.example.restfulapi.model.Address;
import org.example.restfulapi.model.User;
import org.example.restfulapi.repository.criteria.SearchCriteria;

import org.example.restfulapi.repository.criteria.UserSearchQueryCriteriaConsumer;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.restfulapi.util.AppConst.LIKE_FORMAT;
import static org.example.restfulapi.util.AppConst.SORT_BY;

@Repository
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String sortBy, String search) {
        if(pageNo>0)
            pageNo--;
        log.info("Execute search user with keyword={}", search);

        StringBuilder sqlQuery = new StringBuilder("SELECT new org.example.restfulapi.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.phone, u.email) FROM User u WHERE 1=1");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) like lower(:email)");
        }

        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        // Get list of users
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        List<?> users = selectQuery.getResultList();

        log.info("totalElements={}", users.size());
        int totalPage = users.size();

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<?> page = new PageImpl<>(users, pageable,totalPage);

        return PageResponse.builder()
                .pageNo(++pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(users)
                .build();
    }

    public PageResponse<?> searchUserByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        if(pageNo>0)
            pageNo--;
        List<SearchCriteria> criteriaList=new ArrayList<>();
        if(search!=null) {
            for(String s : search) {
                Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3)));
                }
            }
        }
        List<User> users = getUsers(pageNo,pageSize,criteriaList,sortBy,address);


        return PageResponse.builder()
                .pageNo(++pageNo)
                .pageSize(pageSize)
                .totalPage(getTotalElements(criteriaList,address).intValue())
                .items(users)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy,String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryConsumer = new UserSearchQueryCriteriaConsumer(predicate,criteriaBuilder,root);
        criteriaList.forEach(queryConsumer);
        predicate = queryConsumer.getPredicate();

        if(StringUtils.hasLength(address)){
            Join<Address,User> addressUserJoin= root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"),"%"+address+"%");
            query.where(predicate,addressPredicate);
        }
        else {
            query.where(predicate);
        }

        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName=matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc"))
                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                else
                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
            }
        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }
    private Long getTotalElements(List<SearchCriteria> params,String address) {
        log.info("-------------- getTotalElements --------------");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.select(criteriaBuilder.count(root));
        if(StringUtils.hasLength(address)){
            Join<Address,User> addressUserJoin= root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"),"%"+address+"%");
            query.where(predicate,addressPredicate);
        }
        else {
            query.where(predicate);
        }
        return entityManager.createQuery(query).getSingleResult();
    }
}
