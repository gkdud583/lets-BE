package com.lets.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lets.security.UserPrincipal;
import com.lets.service.likePost.LikePostService;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
import com.lets.web.dto.likepost.LikePostResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.user.SettingRequestDto;
import com.lets.web.dto.user.SettingResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final PostService postService;
  private final LikePostService likePostService;

  /**
   * 작성 글 조회
   */
  @GetMapping("/myPosts")
  @PreAuthorize("hasRole('ROLE_USER')")
  public List<PostResponseDto> findMyPosts(@AuthenticationPrincipal UserPrincipal principal) {
    return postService.findUserPosts(principal.getId());
  }

  /**
   * 관심 글 조회
   */
  @GetMapping("/myLikes")
  @PreAuthorize("hasRole('ROLE_USER')")
  public List<LikePostResponseDto> findMyLikes(@AuthenticationPrincipal UserPrincipal principal) {
    return likePostService.findLikePosts(principal.getId());
  }

  @GetMapping("/setting")
  @PreAuthorize("hasRole('ROLE_USER')")
  public SettingResponseDto getSetting(@AuthenticationPrincipal UserPrincipal principal) {
    return userService.getSetting(principal.getId());
  }

  @PatchMapping("/setting")
  @PreAuthorize("hasRole('ROLE_USER')")
  public SettingResponseDto setSetting(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody SettingRequestDto settingDto
  ) {
    return userService.setSetting(principal.getId(), settingDto);
  }
}
