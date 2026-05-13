package com.markup.dinerop.auth.repository;

import com.markup.dinerop.auth.entity.UserPreRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreRegistrationRepository extends JpaRepository<UserPreRegistration, Long> {

    Optional<UserPreRegistration> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
