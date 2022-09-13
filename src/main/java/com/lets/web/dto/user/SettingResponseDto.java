package com.lets.web.dto.user;

import java.util.List;
import java.util.stream.Collectors;

import com.lets.domain.userTechStack.UserTechStack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingResponseDto {
  private String profile;
  private String nickname;
  private List<String> tags;

  private SettingResponseDto(
      String profile,
      String nickname,
      List<UserTechStack> userTechStacks
  ) {
    this.profile = profile;
    this.nickname = nickname;
    this.tags = userTechStacks
        .stream()
        .map(userTechStack -> userTechStack
            .getTag()
            .getName())
        .collect(Collectors.toList());
  }

  public static SettingResponseDto from(
      String profile,
      String nickname,
      List<UserTechStack> userTechStacks
  ) {
    return new SettingResponseDto(profile, nickname, userTechStacks);
  }
}
