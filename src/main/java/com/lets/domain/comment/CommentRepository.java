package com.lets.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lets.domain.post.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
  Long countByPost(Post post);

  void deleteAllByPost(Post post);
}
