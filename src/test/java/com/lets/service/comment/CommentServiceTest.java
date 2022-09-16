package com.lets.service.comment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.security.AuthProvider;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
  @Mock
  CommentRepository commentRepository;

  @Mock
  UserService userService;

  @Mock
  PostService postService;

  @InjectMocks
  CommentService commentService;

  static long userId = 1l;
  static long postId = 1l;
  static long commentId = 1l;
  static String profile = null;
  static String newContent = "content";

  static User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
  static Post post = Post.createPost(user, "title", "content");

  @Test
  @DisplayName("save메서드는 댓글을 저장하고 저장된 댓글을 반환한다")
  void save() {
    // given
    long commentId = 1l;
    Comment comment = Comment.createComment(user, post, "content");

    ReflectionTestUtils.setField(comment, "id", commentId);

    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postService.findById(anyLong()))
        .willReturn(post);
    given(commentRepository.save(any(Comment.class)))
        .willReturn(comment);

    // when
    CommentResponseDto result = commentService.save(
        userId,
        postId,
        new CommentSaveRequestDto(newContent)
    );

    // then
    verify(commentRepository).save(any(Comment.class));
    assertThat(result.getId()).isEqualTo(commentId);
    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result.getNickname()).isEqualTo(user.getNickname());
    assertThat(result.getContent()).isEqualTo(newContent);
  }

  @Test
  @DisplayName("save메서드는 유저가 존재하지 않는다면 예외를 던진다")
  void saveWithNonexistentUser() {
    // given
    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    // when, then
    assertThatThrownBy(() -> {
      commentService.save(userId, postId, new CommentSaveRequestDto("content"));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("save메서드는 글이 존재하지 않는다면 예외를 던진다")
  void saveWithNonexistentPost() {
    // given
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

    // when, then
    assertThatThrownBy(() -> {
      commentService.save(userId, postId, new CommentSaveRequestDto("content"));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("update메서드는 댓글 내용을 수정하고 수정된 댓글을 반환한다")
  void update() {
    // given
    long commentId = 1l;

    Comment comment = Comment.createComment(user, post, "content");

    ReflectionTestUtils.setField(comment, "id", commentId);

    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    CommentResponseDto result = commentService.update(
        commentId,
        new CommentUpdateRequestDto(newContent)
    );

    // then
    assertThat(result.getContent()).isEqualTo(newContent);
  }

  @Test
  @DisplayName("update메서드는 댓글이 존재하지 않는다면 예외를 던진다")
  void updateWithNonexistentComment() {
    // given
    long commentId = 1l;

    String newContent = "newContent";

    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.update(commentId, new CommentUpdateRequestDto(newContent));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("delete메서드는 댓글을 삭제한다")
  void delete() {
    // given
    long commentId = 1l;

    Comment comment = Comment.createComment(user, post, "content");

    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    commentService.delete(commentId);

    // then
    verify(commentRepository).delete(any(Comment.class));
  }

  @Test
  @DisplayName("delete메서드는 댓글이 존재하지 않는다면 예외를 던진다")
  void deleteWithNonexistentComment() {
    // given
    long commentId = 1l;
    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.delete(commentId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findById메서드는 아이디로 댓글을 조회한다")
  void findById() {
    // given
    long commentId = 1l;

    Comment comment = Comment.createComment(user, post, "content");

    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    Comment foundComment = commentService.findById(commentId);

    // then
    verify(commentRepository).findByIdWithUser(anyLong());
    assertThat(foundComment).isEqualTo(comment);
  }

  @Test
  @DisplayName("findById메서드는 댓글이 존재하지 않는다면 예외를 던진다")
  void findByIdNonexistentComment() {
    // given
    long commentId = 1l;
    given(commentRepository.findByIdWithUser(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.findById(commentId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }
}
