package com.lets.service.comment;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
  private final UserService userService;
  private final PostService postService;

  //댓글 저장
  @Transactional
  public Comment save(
      long userId,
      long postId,
      CommentSaveRequestDto commentSaveRequestDto
  ) {
    User user = userService.findById(userId);
    Post post = postService.findById(postId);
    Comment comment = Comment.createComment(user, post, commentSaveRequestDto.getContent());
    Comment savedComment = commentRepository.save(comment);
    return savedComment;
  }

  //댓글 수정
  @Transactional
  public Comment update(
      long commentId,
      CommentUpdateRequestDto commentUpdateRequestDto
  ) {
    Comment comment = findById(commentId);
    comment.change(commentUpdateRequestDto.getContent());
    return comment;
  }

  //댓글 지우기
  @Transactional
  public void delete(long commentId) {
    Comment comment = findById(commentId);
    commentRepository.delete(comment);
  }

  public Comment findById(long id) {
    return commentRepository
        .findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
  }
}
