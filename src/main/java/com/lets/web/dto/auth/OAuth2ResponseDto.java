package com.lets.web.dto.auth;

import com.lets.security.oauth2.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2ResponseDto {
    private boolean LoginSuccess;
    private String message;
    private String socialLoginId;
    private AuthProvider authProvider;

}
