package com.lets.domain.userTechStack;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lets.domain.user.User;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {

  @Modifying
  @Transactional
  @Query("delete from UserTechStack u where u.user = :user")
  void deleteAllByUser(@Param("user") User user);

  @Query("select u from UserTechStack u left join fetch u.tag where u.user = :user")
  List<UserTechStack> findAllByUser(@Param("user") User user);
}
