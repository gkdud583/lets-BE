package com.lets.domain.post;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lets.domain.user.User;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Modifying
  @Transactional
  @Query("delete from Post p where p.id In (:ids)")
  void deleteAllById(@Param("ids") List<Long> ids);

  @Query("select p from Post p where p.user = :user")
  List<Post> findAllByUser(@Param("user") User user);

  @Query("select p from Post p join fetch p.user where p.id = :postId")
  Optional<Post> findOneById(@Param("postId") Long postId);
}
