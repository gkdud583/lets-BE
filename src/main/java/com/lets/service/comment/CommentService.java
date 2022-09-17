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
import com.lets.web.dto.comment.CommentResponseDto;
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
  public CommentResponseDto save(
      long userId,
      long postId,
      CommentSaveRequestDto commentSaveRequestDto
  ) {
    User user = userService.findById(userId);
    Post post = postService.findOneById(postId);
    Comment comment = Comment.createComment(user, post, commentSaveRequestDto.getContent());
    Comment savedComment = commentRepository.save(comment);

    return CommentResponseDto.from(null, savedComment.getId(), user.getNickname(),
                                   savedComment.getContent(), savedComment.getCreatedDate()
    );
  }

  //댓글 수정
  @Transactional
  public CommentResponseDto update(
      long commentId,
      CommentUpdateRequestDto commentUpdateRequestDto
  ) {
    Comment comment = findById(commentId);
    comment.change(commentUpdateRequestDto.getContent());
    return CommentResponseDto.from(null,
                                   comment.getId(),
                                   comment
                                       .getUser()
                                       .getNickname(),
                                   comment.getContent(),
                                   comment.getCreatedDate()
    );
  }

  //댓글 지우기
  @Transactional
  public void delete(long commentId) {
    Comment comment = findById(commentId);
    commentRepository.delete(comment);
  }

  private Comment findById(long id) {
    return commentRepository
        .findByIdWithUser(id)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
  }
}
