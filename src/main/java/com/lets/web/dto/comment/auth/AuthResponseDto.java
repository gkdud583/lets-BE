package com.lets.web.dto.comment.auth;


import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class AuthResponseDto {
    private String nickname;
    private String accessToken;
    private String tokenType = "Bearer";
    private String message;
    
    public AuthResponseDto(String nickname, String accessToken, String message){
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.message = message;
    }
}
