package com.lets.web.dto.user;

import javax.validation.constraints.NotBlank;

import com.lets.security.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;

    private String profile;

    @NotBlank
    private String nickname;


    @NotBlank
    private String socialLoginId;


    private AuthProvider authProvider;


    public static UserResponseDto toDto(String profile, Long id, String nickname, String socialLoginId, AuthProvider authProvider){
        return new UserResponseDto(id, profile, nickname, socialLoginId, authProvider);

    }
}
