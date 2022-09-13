package com.lets.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lets.security.AuthProvider;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u from User u where u.id = :id")
  Optional<User> findById(@Param("id") Long id);

  @Query("SELECT u from User u where u.socialLoginId = :socialLoginId AND u.authProvider = :authProvider")
  Optional<User> findBySocialLoginIdAndAuthProvider(
      @Param("socialLoginId") String socialLoginId,
      @Param("authProvider") AuthProvider authProvider
  );

  Boolean existsByNickname(String nickname);

  Boolean existsBySocialLoginIdAndAuthProvider(
      String socialLoginId,
      AuthProvider authProvider
  );
}
