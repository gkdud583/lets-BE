package com.lets.web.dto.user;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class SettingResponseDto {
  private final String profile;
  private final String nickname;
  private final List<String> tags;

  public static SettingResponseDto from(
      String profile,
      String nickname,
      List<String> tags
  ) {
    return SettingResponseDto.builder()
        .profile(profile)
        .nickname(nickname)
        .tags(tags)
        .build();
  }
}
