package com.lets.web.dto.likepost;

import java.util.List;

import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.PostStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class LikePostResponseDto {
  private final long id;

  private final long likeCount;

  private final long viewCount;

  private final List<String> tags;

  private final PostStatus status;

  private final LikePostStatus likePostStatus;

  private final String title;

  private final String content;


  public static LikePostResponseDto from(
      long id,
      String title,
      String content,
      long likeCount,
      long viewCount,
      PostStatus status,
      LikePostStatus likePostStatus,
      List<String> tags
  ) {
    return LikePostResponseDto.builder()
        .id(id)
        .title(title)
        .content(content)
        .likeCount(likeCount)
        .viewCount(viewCount)
        .status(status)
        .likePostStatus(likePostStatus)
        .tags(tags)
        .build();
  }
}
