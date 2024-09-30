package org.example.restfulapi.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.restfulapi.dto.request.SignInRequest;
import org.example.restfulapi.dto.response.TokenResponse;
import org.example.restfulapi.exception.ResourceNotFoundException;
import org.example.restfulapi.model.Token;
import org.example.restfulapi.model.User;
import org.example.restfulapi.repository.TokenRepository;
import org.example.restfulapi.repository.UserRepository;
import org.example.restfulapi.service.JwtService;
import org.example.restfulapi.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.example.restfulapi.util.TokenType.AccessToken;
import static org.example.restfulapi.util.TokenType.RefreshToken;
import static org.springframework.http.HttpHeaders.REFERER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final JwtService jwtService;
    public TokenResponse authentication(SignInRequest signInRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(()->new ResourceNotFoundException("Username or password incorrect"));
        String accessToken =jwtService.generateToken(user);
        String refreshToken =jwtService.generateRefreshToken(user);
        // save token to db
        tokenService.save(Token.builder()
                        .username(user.getUsername())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userID(user.getId())
                .build();
    }
    public TokenResponse refresh(HttpServletRequest request){
        String refreshToken = request.getHeader(REFERER);
        if(StringUtils.isBlank(refreshToken)){
            throw new ResourceNotFoundException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken,RefreshToken);
        Optional<User> user  = userRepository.findByUsername(userName);
        if(!jwtService.isValid(refreshToken,user.get(),RefreshToken)){
            throw new ResourceNotFoundException("Token is Invalid");
        }

        String accessToken =jwtService.generateToken(user.get());
        tokenService.save(Token.builder()
                .username(user.get().getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userID(user.get().getId())
                .build();
    }

    public String logout(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFERER);
        if(StringUtils.isBlank(refreshToken)){
            throw new ResourceNotFoundException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken,AccessToken);
        tokenService.delete(tokenService.getByUsername(userName));
        return  "Deleted";
    }
}
