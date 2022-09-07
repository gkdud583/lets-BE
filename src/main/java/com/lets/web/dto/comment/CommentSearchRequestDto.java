package com.lets.web.dto.comment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lets.domain.post.Post;

import lombok.Getter;

@Getter
public class CommentSearchRequestDto {
  private Post post;

  @JsonCreator
  public CommentSearchRequestDto(Post post) {
    this.post = post;
  }
}
