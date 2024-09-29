package org.example.restfulapi.repository;

import org.example.restfulapi.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByUsername(String username);
}
