package com.lets.web.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lets.security.UserPrincipal;
import com.lets.service.post.PostService;
import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.ChangePostStatusResponseDto;
import com.lets.web.dto.post.PostCommentResponseDto;
import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostRecommendResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.post.PostSaveRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;
import com.lets.web.dto.post.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
  private final PostService postService;

  /**
   * 글 검색
   */
  @GetMapping("/filter")
  public List<PostResponseDto> searchPosts(
      @ModelAttribute PostSearchRequestDto search,
      @PageableDefault(size = 20, sort = {
          "createdDate"}, direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return postService.searchPosts(search, pageable);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_USER')")
  public PostResponseDto savePost(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestBody PostSaveRequestDto postSaveRequestDto
  ) {
    return postService.savePost(principal.getId(), postSaveRequestDto);
  }

  @PutMapping("/{postId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public PostResponseDto updatePost(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId,
      @RequestBody PostUpdateRequestDto postUpdateRequestDto
  ) {
    return postService.updatePost(principal.getId(), postId, postUpdateRequestDto);
  }

  @GetMapping("/{postId}")
  public PostCommentResponseDto findPost(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId
  ) {
    if (principal == null) {
      return postService.findPost(null, postId);
    }

    return postService.findPost(principal.getId(), postId);
  }

  @DeleteMapping("/{postId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public void deletePost(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId
  ) {
    postService.deletePost(principal.getId(), postId);
  }

  @PostMapping("/{postId}/likes")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ChangeLikePostStatusResponseDto changeLikeStatus(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId
  ) {
    return postService.changeLikeStatus(principal.getId(), postId);
  }

  @PostMapping("/{postId}/status")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ChangePostStatusResponseDto changePostStatus(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId
  ) {
    return ChangePostStatusResponseDto.of(postService.changePostStatus(principal.getId(), postId));
  }


  @GetMapping("/{postId}/recommends")
  public List<PostRecommendResponseDto> recommendPosts(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("postId") long postId,
      @ModelAttribute PostRecommendRequestDto postRecommendRequestDto
  ) {
    if (principal == null) {
      return postService.recommendPosts(null, postId, postRecommendRequestDto);
    }
    return postService.recommendPosts(principal.getId(), postId, postRecommendRequestDto);
  }
}
