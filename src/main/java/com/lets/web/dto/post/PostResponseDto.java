package com.lets.web.dto.post;

import java.util.List;

import com.lets.domain.post.PostStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 글 검색 응답에 사용하는 DTO
 * 글 검색시 LikePostStatus는 필요없으므로 추가하지 않았음.
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostResponseDto {
  private final String profile;

  private final long id;

  private final long likeCount;

  private final long viewCount;

  private final List<String> tags;

  private final PostStatus status;

  private final String title;

  private final String content;

  private final long commentCount;

  public static PostResponseDto from(
      String profile,
      long id,
      String title,
      String content,
      long likeCount,
      long viewCount,
      PostStatus status,
      List<String> tags,
      long commentCount
  ) {
    return PostResponseDto.builder()
        .profile(profile)
        .id(id)
        .title(title)
        .content(content)
        .likeCount(likeCount)
        .viewCount(viewCount)
        .status(status)
        .tags(tags)
        .commentCount(commentCount)
        .build();
  }
}
