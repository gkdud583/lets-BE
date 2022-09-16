package com.lets.web.dto.post;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostSearchRequestDto {
  private final String status;
  private final List<String> tags;
}
