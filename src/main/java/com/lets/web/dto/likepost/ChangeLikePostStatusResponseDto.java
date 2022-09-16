package com.lets.web.dto.likepost;

import com.lets.domain.likePost.LikePostStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChangeLikePostStatusResponseDto {
  private final long likeCount;
  private final LikePostStatus likePostStatus;

  public static ChangeLikePostStatusResponseDto of (long likeCount, LikePostStatus likePostStatus) {
    return ChangeLikePostStatusResponseDto.builder()
        .likeCount(likeCount)
        .likePostStatus(likePostStatus)
        .build();
  }
}
