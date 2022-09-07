package com.lets.web.dto.comment;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class CommentSaveRequestDto {
  private String content;

  @JsonCreator
  public CommentSaveRequestDto(String content) {
    this.content = content;
  }
}
