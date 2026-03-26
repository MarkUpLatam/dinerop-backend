package com.markup.dinerop.auth.repository;

import com.markup.dinerop.auth.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByTokenAndUsedFalse(String token);

    Optional<ActivationToken> findByUser_IdUserAndUsedFalse(Long userId);
}

