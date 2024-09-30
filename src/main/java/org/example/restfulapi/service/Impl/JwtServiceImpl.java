package org.example.restfulapi.service.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.restfulapi.model.Token;
import org.example.restfulapi.repository.TokenRepository;
import org.example.restfulapi.service.JwtService;
import org.example.restfulapi.service.TokenService;
import org.example.restfulapi.util.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.example.restfulapi.util.TokenType.AccessToken;
import static org.example.restfulapi.util.TokenType.RefreshToken;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.expiryHour}")
    private Long expiryHour;

    @Value("${jwt.expiryDay}")
    private Long expiryDay;

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.secretRefreshKey}")
    private String secretRefreshKey;
    private final TokenRepository tokenRepository;
    @Override
    public String generateToken(UserDetails userDetails) {
        //TODO xu ly tao token
        return generateToken(new HashMap<>(),userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(),userDetails);
    }

    @Override
    public String extractUsername(String token,TokenType type) {
        return extractClaim(token, Claims::getSubject,type);
    }

    @Override
    public boolean isValid(String token, UserDetails user,TokenType type) {
        final String username = extractUsername(token,type);
        Optional<Token> tokenOptional = tokenRepository.findByUsername(username);
        boolean checkToken;
        if(AccessToken.equals(type))
            checkToken =tokenOptional.get().getAccessToken().equals(token);
        else
            checkToken =tokenOptional.get().getRefreshToken().equals(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token,type) && checkToken);
    }
    @Override
    public boolean isTokenExpired(String token,TokenType type) {
        return  extractExpiration(token,type).before(new Date());
    }

    private String generateToken(Map<String,Object> claims,UserDetails userDetails){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*expiryHour))
                .signWith(getKey(AccessToken), SignatureAlgorithm.HS256)
                .compact();
    }
    private String generateRefreshToken(Map<String,Object> claims,UserDetails userDetails){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24*expiryDay))
                .signWith(getKey(RefreshToken), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getKey(TokenType type){
        byte[] keyBytes ;
        if (AccessToken.equals(type)) {
            keyBytes = Decoders.BASE64.decode(secretKey);
        }
        else
            keyBytes = Decoders.BASE64.decode(secretRefreshKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver,TokenType type){
        final Claims claims = extraAllClaim(token,type);
        return claimResolver.apply(claims);
    }
    private Claims extraAllClaim(String token, TokenType type){
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }
    private Date extractExpiration(String token,TokenType type) {
        return  extractClaim(token, Claims::getExpiration,type);
    }
}
