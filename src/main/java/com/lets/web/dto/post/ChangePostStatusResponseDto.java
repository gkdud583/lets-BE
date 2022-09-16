package com.lets.web.dto.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lets.domain.post.PostStatus;

import lombok.Getter;

@Getter
public class ChangePostStatusResponseDto {
  private PostStatus status;

  @JsonCreator
  private ChangePostStatusResponseDto(PostStatus status) {
    this.status = status;
  }

  public static ChangePostStatusResponseDto of(PostStatus status) {
    return new ChangePostStatusResponseDto(status);
  }
}
