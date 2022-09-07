package com.lets.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lets.security.UserPrincipal;
import com.lets.service.comment.CommentService;
import com.lets.web.dto.ApiResponseDto;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {
  private final CommentService commentService;

  @PostMapping("/{postId}/comments")
  @PreAuthorize("hasRole('ROLE_USER')")
  public CommentResponseDto save(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable long postId,
      @RequestBody CommentSaveRequestDto commentSaveRequestDto
  ) {
    return CommentResponseDto.from(commentService.save(
        principal.getId(),
        postId,
        commentSaveRequestDto
    ), null);
  }

  @PutMapping("/{postId}/comments/{commentId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public CommentResponseDto update(
      @PathVariable long commentId,
      @RequestBody CommentUpdateRequestDto commentUpdateRequestDto
  ) {
    return CommentResponseDto.from(commentService.update(
        commentId,
        commentUpdateRequestDto
    ), null);
  }

  @DeleteMapping("/{postId}/comments/{commentId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ApiResponseDto delete(
      @PathVariable long commentId
  ) {
    commentService.delete(commentId);

    return new ApiResponseDto(true, "댓글이 삭제되었습니다.");
  }
}
