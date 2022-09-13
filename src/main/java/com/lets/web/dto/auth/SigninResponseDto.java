package com.lets.web.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class SigninResponseDto {
    private String profile;
    private String nickname;
    private String accessToken;
    private String tokenType = "Bearer";
    private String message;

    public SigninResponseDto(String profile, String nickname, String accessToken, String message){
        this.profile = profile;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.message = message;
    }
}
