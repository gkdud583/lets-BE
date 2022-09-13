package com.lets.web.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lets.security.UserPrincipal;
import com.lets.service.likePost.LikePostService;
import com.lets.service.post.PostService;
import com.lets.service.user.UserService;
import com.lets.service.userTechStack.UserTechStackService;
import com.lets.util.CloudinaryUtil;
import com.lets.util.FileUtil;
import com.lets.web.dto.likepost.LikePostResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.user.SettingResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final PostService postService;
  private final LikePostService likePostService;
  private final UserTechStackService userTechStackService;
  private final CloudinaryUtil cloudinaryUtil;
  private final FileUtil fileUtil;

  /**
   * 작성 글 조회
   */
  @GetMapping("/myPosts")
  @PreAuthorize("hasRole('ROLE_USER')")
  public List<PostResponseDto> findMyPosts(@AuthenticationPrincipal UserPrincipal principal) {
    return postService.findPosts(principal.getId());
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

  // @PatchMapping("/setting")
  // @PreAuthorize("hasRole('ROLE_USER')")
  // public SettingResponseDto setSetting(
  //     @AuthenticationPrincipal UserPrincipal principal,
  //     @Valid @RequestBody SettingRequestDto settingDto
  // ) {
  //
  //   File file = null;
  //   String profileStatus = "PRIVATE";
  //
  //   String profile = settingDto.getProfile();
  //
  //   //"KEEP" -> 기존 이미지 유지
  //   //"PUBLIC" -> 기본 이미지로 변경
  //   //그 외 -> 새로운 이미지로 변경
  //   if (profile.equals("KEEP") || profile.equals("PUBLIC")) {
  //     profileStatus = profile;
  //   } else {
  //     //file변환
  //     file = fileUtil.decodeFile(settingDto.getProfile());
  //   }
  //
  //   //유저 조회
  //   User findUser = userService.findById(principal.getId());
  //
  //   //설정 변경
  //   userService.change(findUser, profileStatus, file, settingDto);
  //
  //   profile = cloudinaryUtil.findFileURL(findUser.getPublicId());
  //
  //   //태그 조회
  //   List<UserTechStack> userTechStacks = userTechStackService.findAllByUser(findUser);
  //
  //   return SettingResponseDto.toDto(profile, findUser.getNickname(), userTechStacks);
  // }

}
