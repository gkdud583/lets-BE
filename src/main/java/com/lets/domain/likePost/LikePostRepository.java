package com.lets.domain.likePost;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lets.domain.post.Post;
import com.lets.domain.user.User;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {
  @Query("select l from LikePost l join fetch l.post where l.user = :user")
  List<LikePost> findAllByUser(@Param("user") User user);

  @Modifying
  @Transactional
  @Query("delete from LikePost l where l.post in (:posts)")
  void deleteAllByPost(@Param("posts") List<Post> posts);

  @Query("select l from LikePost l join fetch l.post")
  Optional<LikePost> findByUserIdAndPostId(
      Long userId,
      Long postId
  );
}
