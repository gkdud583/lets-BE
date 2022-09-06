package com.lets.web.dto.user;

import com.lets.domain.userTechStack.UserTechStack;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SettingResponseDto {
    private String profile;
    private String nickname;
    private List<String> tags = new ArrayList<>();

    public SettingResponseDto(String profile, String nickname, List<UserTechStack> userTechStacks){
        this.profile = profile;
        this.nickname = nickname;
        this.tags = userTechStacks.stream().map(userTechStack -> userTechStack.getTag().getName()).collect(Collectors.toList());
    }
    public static SettingResponseDto toDto(String profile, String nickname, List<UserTechStack> userTechStacks){
        return new SettingResponseDto(profile, nickname, userTechStacks);
    }

}
