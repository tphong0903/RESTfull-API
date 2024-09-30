package org.example.restfulapi.service;

import org.example.restfulapi.dto.request.UserRequestDTO;
import org.example.restfulapi.dto.response.PageResponse;
import org.example.restfulapi.dto.response.UserDetailResponse;
import org.example.restfulapi.model.User;
import org.example.restfulapi.util.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {

    UserDetailsService userDetailService();

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    User getUserById(long id);

    UserDetailResponse getUser(long userId);
//    List<UserDetailResponse> getAllUsersWithSortBy(int pageNo, int pageSize,String sortBy);
//    List<UserDetailResponse> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize,String... sortBy);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String sort,String search);

    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);
//
//    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);
}
