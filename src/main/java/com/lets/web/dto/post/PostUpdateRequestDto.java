package com.lets.web.dto.post;

import java.util.List;

import com.lets.domain.post.PostStatus;

import lombok.Getter;

@Getter
public class PostUpdateRequestDto {
  private String title;
  private String content;
  private List<String> tags;
  private PostStatus status;

  public PostUpdateRequestDto(
      String title,
      String content,
      List<String> tags,
      PostStatus status
  ) {
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.status = status;
  }
}
