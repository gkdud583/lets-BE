package com.lets.web.dto.auth;


import com.lets.security.oauth2.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String socialLoginId;

    @NotNull
    private AuthProvider authProvider;
}
