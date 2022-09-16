package com.lets.web.dto.auth;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.lets.security.AuthProvider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupRequestDto {

  @NotBlank
  private final String profile;

  @NotBlank
  private final String nickname;

  @NotBlank
  private final String socialLoginId;

  @NotNull
  private final AuthProvider authProvider;
  private final List<String> tags;

}
