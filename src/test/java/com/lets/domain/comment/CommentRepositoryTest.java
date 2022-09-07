package com.lets.domain.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lets.config.QueryDslConfig;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.oauth2.AuthProvider;
import com.lets.web.dto.comment.CommentSearchRequestDto;

@DataJpaTest
@Import(QueryDslConfig.class)
public class CommentRepositoryTest {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostRepository postRepository;

  private User user;
  private Post post;
  private Comment comment;

  @BeforeEach
  void setup() {
    user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    post = Post.createPost(user, "title1", "content1");
    postRepository.save(post);

    comment = Comment.createComment(user, post, "comment1");
    commentRepository.save(comment);
  }

  @DisplayName("countByPost메서드는 글의 댓글 수를 반환한다")
  @Test
  public void countByPost() {
    //given

    //when
    Long count = commentRepository.countByPost(post);

    //then
    assertThat(count).isEqualTo(1);
  }

  @DisplayName("deleteAllByPost메서드는 특정 글의 모든 댓글을 삭제한다")
  @Test
  public void deleteAllByPost() {
    //given

    //when
    commentRepository.deleteAllByPost(post);

    //then
    Long result = commentRepository.countByPost(post);
    assertThat(result).isEqualTo(0);
  }

  @DisplayName("findComments메서드는 모든 댓글을 조회한다")
  @Test
  public void findComments() {
    //given
    Comment comment2 = Comment.createComment(user, post, "comment2");
    commentRepository.save(comment2);

    //when
    List<Comment> comments = commentRepository.findComments(new CommentSearchRequestDto(post));

    //then
    assertThat(comments.size()).isEqualTo(2);
  }

}
