package com.lets.web.dto.auth;


import com.lets.security.oauth2.AuthProvider;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    private String profile;

    @NotBlank
    private String nickname;

    @NotBlank
    private String socialLoginId;

    @NotNull
    private AuthProvider authProvider;
    private List<String> tags = new ArrayList<>();

    public void setTags(List<String> tags){
        this.tags = tags;
    }
}
