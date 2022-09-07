package com.lets.web.dto.comment;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {
  private String content;

  @JsonCreator
  public CommentUpdateRequestDto(String content) {
    this.content = content;
  }
}
