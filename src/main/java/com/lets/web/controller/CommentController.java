package com.lets.web.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lets.security.UserPrincipal;
import com.lets.service.comment.CommentService;
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
    return commentService.save(principal.getId(), postId, commentSaveRequestDto);
  }

  @PutMapping("/{postId}/comments/{commentId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public CommentResponseDto update(
      @PathVariable long commentId,
      @RequestBody CommentUpdateRequestDto commentUpdateRequestDto
  ) {
    return commentService.update(commentId, commentUpdateRequestDto);
  }

  @DeleteMapping("/{postId}/comments/{commentId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  @ResponseStatus(OK)
  public void delete(
      @PathVariable long commentId
  ) {
    commentService.delete(commentId);
  }
}
