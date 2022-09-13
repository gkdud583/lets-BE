package com.lets.web.dto.post;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostSearchRequestDto {
  private String status;
  private List<String> tags;
}
