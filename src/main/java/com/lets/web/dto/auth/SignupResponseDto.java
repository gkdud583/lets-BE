package com.lets.web.dto.auth;

import com.lets.security.AuthProvider;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SignupResponseDto {
  private final long id;
  private final String profile;
  private final String nickname;
  private final String socialLoginId;
  private final AuthProvider authProvider;

  public static SignupResponseDto from(
      long id,
      String profile,
      String nickname,
      String socialLoginId,
      AuthProvider authProvider
  ) {
    return SignupResponseDto
        .builder()
        .id(id)
        .profile(profile)
        .nickname(nickname)
        .socialLoginId(socialLoginId)
        .authProvider(authProvider)
        .build();
  }
}
