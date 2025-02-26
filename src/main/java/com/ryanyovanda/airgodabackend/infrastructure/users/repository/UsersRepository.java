package com.ryanyovanda.airgodabackend.infrastructure.users.repository;

import com.ryanyovanda.airgodabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailContainsIgnoreCase(String email);

  // ✅ Find user by verification token
  Optional<User> findByVerificationToken(String verificationToken);
}
