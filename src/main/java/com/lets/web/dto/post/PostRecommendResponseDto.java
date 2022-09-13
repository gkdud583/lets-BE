package com.lets.web.dto.post;

import com.lets.domain.post.Post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PostRecommendResponseDto {
  private Long id;

  private String title;

  public PostRecommendResponseDto(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
  }

  public static PostRecommendResponseDto PostRecommendToDto(Post post) {
    return new PostRecommendResponseDto(post);
  }
}
