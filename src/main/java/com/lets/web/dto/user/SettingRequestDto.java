package com.lets.web.dto.user;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SettingRequestDto {
  @NotBlank
  private final String profile;
  @NotBlank
  private final String nickname;
  private final List<String> tags;
}
