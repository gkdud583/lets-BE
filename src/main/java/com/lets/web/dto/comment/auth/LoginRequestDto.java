package com.lets.web.dto.comment.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.lets.security.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String socialLoginId;

    @NotNull
    private AuthProvider authProvider;
}
