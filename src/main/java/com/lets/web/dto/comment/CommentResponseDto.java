package com.lets.web.dto.comment;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommentResponseDto {
  private final String profile;
  private final Long id;
  private final String nickname;
  private final String content;
  private final LocalDateTime createdTime;

  public static CommentResponseDto from(
      String profile,
      long id,
      String nickname,
      String content,
      LocalDateTime createdTime
  ) {
    return CommentResponseDto
        .builder()
        .profile(profile)
        .id(id)
        .nickname(nickname)
        .content(content)
        .createdTime(createdTime)
        .build();
  }
}
