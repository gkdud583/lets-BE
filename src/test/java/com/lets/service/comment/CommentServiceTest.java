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

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.security.oauth2.AuthProvider;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
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

  @Test
  @DisplayName("save메서드는 댓글을 저장하고 저장된 댓글을 반환한다")
  void saveComment() {
    // given
    User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
    long userId = 1l;
    long postId = 1l;

    Post post = Post.createPost(user, "title", "content");

    Comment comment = Comment.createComment(user, post, "content");

    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postService.findById(anyLong()))
        .willReturn(post);
    given(commentRepository.save(any(Comment.class)))
        .willReturn(comment);

    // when
    Comment savedComment = commentService.save(
        userId,
        postId,
        new CommentSaveRequestDto("comment1")
    );

    // then
    verify(commentRepository).save(any(Comment.class));
    assertThat(savedComment).isEqualTo(comment);
  }

  @Test
  @DisplayName("save메서드는_유저가_존재하지_않는다면_예외를_던진다")
  void saveCommentWithNonexistentUser() {
    // given
    long userId = 1l;
    long postId = 1l;

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
  @DisplayName("save메서드는_글이_존재하지_않는다면_예외를_던진다")
  void saveCommentWithNonexistentPost() {
    // given
    User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
    long userId = 1l;
    long postId = 1l;

    Post post = Post.createPost(user, "title", "content");

    Comment comment = Comment.createComment(user, post, "content");

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
  @DisplayName("update메서드는_댓글_내용을_수정하고_수정된_댓글을_반환한다")
  void updateComment() {
    // given
    User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
    long commentId = 1l;

    Post post = Post.createPost(user, "title", "content");

    Comment comment = Comment.createComment(user, post, "content");

    String newContent = "newContent";

    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    Comment updatedComment = commentService.update(
        commentId,
        new CommentUpdateRequestDto(newContent)
    );

    // then
    assertThat(updatedComment.getContent()).isEqualTo(newContent);
  }

  @Test
  @DisplayName("update메서드는_댓글이_존재하지_않는다면_예외를_던진다")
  void updateCommentWithNonexistentComment() {
    // given
    long commentId = 1l;

    String newContent = "newContent";

    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.update(commentId, new CommentUpdateRequestDto(newContent));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("delete메서드는_댓글을_삭제한다")
  void deleteComment() {
    // given
    User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
    long commentId = 1l;

    Post post = Post.createPost(user, "title", "content");

    Comment comment = Comment.createComment(user, post, "content");

    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    commentService.delete(commentId);

    // then
    verify(commentRepository).delete(any(Comment.class));
  }

  @Test
  @DisplayName("delete메서드는_댓글이_존재하지_않는다면_예외를_던진다")
  void deleteCommentWithNonexistentComment() {
    // given
    long commentId = 1l;
    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.delete(commentId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findById메서드는_아이디로_댓글을_조회한다")
  void findById() {
    // given
    User user = User.createUser("nickname", "123", AuthProvider.google, "PUBLIC");
    long commentId = 1l;

    Post post = Post.createPost(user, "title", "content");

    Comment comment = Comment.createComment(user, post, "content");

    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.of(comment));

    // when
    Comment foundComment = commentService.findById(commentId);

    // then
    verify(commentRepository).findById(anyLong());
    assertThat(foundComment).isEqualTo(comment);
  }

  @Test
  @DisplayName("findById메서드는_댓글이_존재하지_않는다면_예외를_던진다")
  void findByIdNonexistentComment() {
    // given
    long commentId = 1l;
    given(commentRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      commentService.findById(commentId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 댓글을 찾을 수 없습니다.");
  }
}
