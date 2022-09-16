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
  private final String profile;

  private final long id;

  private final long likeCount;

  private final long viewCount;

  private final List<String> tags;

  private final PostStatus status;

  private final String title;

  private final String content;

  private final LikePostStatus likePostStatus;

  private LocalDateTime createdDate;

  private List<CommentResponseDto> comments;

  private String nickname;

  public static PostCommentResponseDto from(
      String profile,
      long id,
      long likeCount,
      long viewCount,
      List<String> tags,
      PostStatus status,
      String title,
      String content,
      LikePostStatus likePostStatus,
      LocalDateTime createdDate,
      List<CommentResponseDto> comments,
      String nickName
  ) {
    return PostCommentResponseDto
        .builder()
        .profile(profile)
        .id(id)
        .viewCount(viewCount)
        .likeCount(likeCount)
        .tags(tags)
        .status(status)
        .title(title)
        .content(content)
        .likePostStatus(likePostStatus)
        .createdDate(createdDate)
        .comments(comments)
        .nickname(nickName)
        .build();
  }
}
