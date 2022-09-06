package com.lets.web.controller;

import com.lets.security.UserPrincipal;
import com.lets.service.comment.CommentService;
import com.lets.web.dto.ApiResponseDto;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto save(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId") Long postId, @RequestBody CommentSaveRequestDto commentSaveRequestDto){
        return CommentResponseDto.CommentToDto(commentService.save(principal, postId, commentSaveRequestDto), null);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentResponseDto update(@PathVariable("postId") Long postId, @PathVariable Long commentId,
                       @RequestBody CommentUpdateRequestDto commentUpdateRequestDto){
        return CommentResponseDto.CommentToDto(commentService.update(commentId, commentUpdateRequestDto) , null);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponseDto delete(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId){
        commentService.delete(commentId);

        return new ApiResponseDto(true, "댓글이 삭제되었습니다.");
    }
}
