package com.lets.web.dto.post;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostRecommendRequestDto {
  private final List<String> tags;
}
