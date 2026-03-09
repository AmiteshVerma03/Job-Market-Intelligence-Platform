package com.jobseeker.repository;

import com.jobseeker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Only one method — always case-insensitive for consistent behaviour
    Optional<User> findByEmailIgnoreCase(String email);
}
