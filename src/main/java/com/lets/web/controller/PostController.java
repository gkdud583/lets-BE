package com.lets.web.controller;

import com.lets.domain.user.User;
import com.lets.security.UserPrincipal;
import com.lets.service.post.PostService;

import com.lets.service.user.UserService;
import com.lets.web.dto.*;

import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;

    /**
     * 글 검색
     */
    @GetMapping("/filter")
    public List<PostResponseDto> searchPosts(@ModelAttribute PostSearchRequestDto search, @PageableDefault(size = 20, sort={"createdDate"}, direction = Sort.Direction.DESC) Pageable pageable){
        return postService.searchPosts(search, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto savePost(@AuthenticationPrincipal UserPrincipal principal, @RequestBody PostSaveRequestDto postSaveRequestDto){
        User findUser = userService.findById(principal.getId());
        return postService.savePost(findUser, postSaveRequestDto);
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public PostResponseDto updatePost(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId") Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto){
        Long userId = principal.getId();
        User user = userService.findById(userId);
        return postService.updatePost(user, postId, postUpdateRequestDto);
    }

    @GetMapping("/{postId}")
    public PostCommentResponseDto findPost(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId") Long postId){
        User findUser = null;
        if(principal != null)
            findUser = userService.findById(principal.getId());

        return postService.findPost(findUser, postId);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponseDto deletePost(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId")Long postId){
        Long userId = principal.getId();
        postService.deletePost(userId, postId);

        return new ApiResponseDto(true, "게시글이 삭제 되었습니다.");
    }

    @PostMapping("/{postId}/likes")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ChangeLikePostStatusResponseDto changeLikeStatus(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId")Long postId){
        return postService.changeLikeStatus(principal.getId(), postId);
    }

    @GetMapping("/{postId}/recommends")
    public List<PostRecommendResponseDto> recommendedPosts(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("postId") Long id, @ModelAttribute PostRecommendRequestDto postRecommendRequestDto){
        Long userId = null;
        if(principal != null)
            userId = principal.getId();
        return  postService.recommendedPosts(userId, id, postRecommendRequestDto);
    }
}
