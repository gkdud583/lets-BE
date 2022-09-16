package com.lets.web.dto.post;

import java.time.LocalDateTime;
import java.util.List;

import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.PostStatus;
import com.lets.web.dto.comment.CommentResponseDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostCommentResponseDto {
  private String profile;

  private Long id;

  private Long likeCount;

  private Long viewCount;

  private List<String> tags;

  private PostStatus status;

  private String title;

  private String content;

  private LikePostStatus likePostStatus;

  private LocalDateTime createdDate;

  private List<CommentResponseDto> comments;

  private String nickname;

  public static PostCommentResponseDto from(
      PostResponseDto postResponseDto,
      LikePostStatus likePostStatus,
      LocalDateTime createdDate,
      List<CommentResponseDto> comments,
      String nickName
  ) {
    return PostCommentResponseDto
        .builder()
        .profile(postResponseDto.getProfile())
        .id(postResponseDto.getId())
        .viewCount(postResponseDto.getViewCount())
        .likeCount(postResponseDto.getLikeCount())
        .tags(postResponseDto.getTags())
        .status(postResponseDto.getStatus())
        .title(postResponseDto.getTitle())
        .content(postResponseDto.getContent())
        .likePostStatus(likePostStatus)
        .createdDate(createdDate)
        .comments(comments)
        .nickname(nickName)
        .build();
  }
}
