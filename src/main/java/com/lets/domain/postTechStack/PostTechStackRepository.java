package com.lets.domain.postTechStack;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lets.domain.post.Post;
import com.lets.domain.user.User;

public interface PostTechStackRepository
    extends JpaRepository<PostTechStack, Long>, PostTechStackCustomRepository {

  @Modifying
  @Transactional
  @Query("delete from PostTechStack p where p.post in (:posts)")
  int deleteAllByPost(@Param("posts") List<Post> posts);

  @Query("select p from PostTechStack p join fetch p.tag join fetch p.post where p.post in (:posts)")
  List<PostTechStack> findAllByPosts(@Param("posts") List<Post> posts);

  @Query("select p from PostTechStack p join fetch p.tag join fetch p.post where p.post.user = :user")
  List<PostTechStack> findAllByUser(@Param("user") User user);
}
