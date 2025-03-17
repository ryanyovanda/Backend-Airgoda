package com.ryanyovanda.airgodabackend.infrastructure.users.repository;

import com.ryanyovanda.airgodabackend.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

  @Modifying
  @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.id = :userId")
  void updateUserImage(@Param("userId") Long userId, @Param("imageUrl") String imageUrl);

  Optional<User> findByEmailContainsIgnoreCase(String email);


  // ✅ Find user by verification token
  Optional<User> findByVerificationToken(String verificationToken);
  Optional<User> findByResetToken(String resetToken);
}
