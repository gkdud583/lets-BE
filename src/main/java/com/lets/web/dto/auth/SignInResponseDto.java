package com.lets.web.dto.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SignInResponseDto {
  private final String profile;
  private final String nickname;
  private final String accessToken;
  private final String tokenType = "Bearer";
  private final String message;

  public static SignInResponseDto from(String profile, String nickname, String accessToken, String message) {
    return SignInResponseDto.builder()
        .profile(profile)
        .nickname(nickname)
        .accessToken(accessToken)
        .message(message)
        .build();
  }
}
