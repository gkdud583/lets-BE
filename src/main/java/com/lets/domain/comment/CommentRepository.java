package com.lets.domain.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lets.domain.post.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
  Long countByPost(Post post);

  void deleteAllByPost(Post post);

  @Query("select c from Comment c join fetch c.user where c.id = :id")
  Optional<Comment> findByIdWithUser(@Param("id") long id);
}
