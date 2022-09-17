package com.lets.web.dto.post;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostUpdateRequestDto {
  private final String title;
  private final String content;
  private final List<String> tags;
}
