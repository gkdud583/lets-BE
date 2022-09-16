package com.lets.web.dto.post;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostSaveRequestDto {
  @NotBlank
  private final String title;
  @NotBlank
  private final String content;
  private final List<String> tags;

}
