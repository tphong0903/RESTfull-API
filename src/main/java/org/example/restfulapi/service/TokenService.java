package org.example.restfulapi.service;

import org.example.restfulapi.exception.ResourceNotFoundException;
import org.example.restfulapi.model.Token;
import org.example.restfulapi.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public void save(Token token) {
        Optional<Token> tokenOptional = tokenRepository.findByUsername(token.getUsername());
        if (tokenOptional.isEmpty()) {
            tokenRepository.save(token);
        } else {
            Token currentToken = tokenOptional.get();
            currentToken.setAccessToken(token.getAccessToken());
            currentToken.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(currentToken);
        }
    }
    public String delete(Token token) {
        tokenRepository.delete(token);
        return "Deleted";
    }
    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }
}
