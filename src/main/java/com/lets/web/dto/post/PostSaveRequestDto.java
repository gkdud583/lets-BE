package com.lets.web.dto.post;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostSaveRequestDto {
  @NotBlank
  private String title;
  @NotBlank
  private String content;
  private List<String> tags;

}
