package com.kien.auth.repository;

import com.kien.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken,Integer> {

    Optional<RefreshToken> findByToken(String token);

}
