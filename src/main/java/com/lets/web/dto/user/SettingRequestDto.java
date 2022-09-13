package com.lets.web.dto.user;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettingRequestDto {
  @NotBlank
  private String profile;
  @NotBlank
  private String nickname;
  private List<String> tags;
}
