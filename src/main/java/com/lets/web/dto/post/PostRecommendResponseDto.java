package com.lets.web.dto.post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PostRecommendResponseDto {
  private final long id;

  private final String title;

  public static PostRecommendResponseDto PostRecommendToDto(long id, String title) {
    return PostRecommendResponseDto.builder()
        .id(id)
        .title(title)
        .build();
  }
}
