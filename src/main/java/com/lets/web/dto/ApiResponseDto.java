package com.lets.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDto {
  private boolean success;
  private String message;

  public static ApiResponseDto from(boolean success, String message) {
    return ApiResponseDto.builder()
        .success(success)
        .message(message)
        .build();
  }
}
