package com.lets.web.dto.auth;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.lets.security.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
