package com.lets.web.dto.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.lets.security.AuthProvider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDto {
  @NotBlank
  private final String socialLoginId;

  @NotNull
  private final AuthProvider authProvider;
}
