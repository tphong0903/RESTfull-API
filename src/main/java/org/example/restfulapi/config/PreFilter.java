package org.example.restfulapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.restfulapi.service.JwtService;
import org.example.restfulapi.service.UserService;
import org.example.restfulapi.util.TokenType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("---------------- PreFilter ----------------");

        // TODO verify token
        final  String authorization = request.getHeader("Authorization");
        if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        final  String token = authorization.substring("Bearer ".length());
        final  String userName = jwtService.extractUsername(token, TokenType.AccessToken);
        if(StringUtils.isNotEmpty(userName) & SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = userService.userDetailService().loadUserByUsername(userName);// check userName có trong db ko
            if(jwtService.isValid(token,userDetails,TokenType.AccessToken)){
                SecurityContext context = SecurityContextHolder.createEmptyContext();// tạo context mới
                // khai báo 1 authentication provider
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                // set các thông tin của request vào userDetails
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // tiến hình xác thực
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
