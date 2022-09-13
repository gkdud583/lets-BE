package com.lets.web.dto.auth;

import com.lets.security.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String profile;
    private String nickname;
    private String socialLoginId;
    private AuthProvider authProvider;


}
