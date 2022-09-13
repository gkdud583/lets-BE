package com.lets.web.dto.likepost;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostStatus;
import com.lets.domain.tag.Tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class LikePostResponseDto {
  private Long id;

  private Long likeCount;

  private Long viewCount;

  private List<String> tags = new ArrayList<>();

  private PostStatus status;

  private LikePostStatus likePostStatus;

  private String title;

  private String content;

  public LikePostResponseDto(
      Post post,
      LikePostStatus likePostStatus,
      List<Tag> tags
  ) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.likeCount = post.getLikeCount();
    this.viewCount = post.getViewCount();
    this.status = post.getStatus();
    this.likePostStatus = likePostStatus;
    this.tags = tags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());
  }

  public static LikePostResponseDto likePostToDto(
      Post post,
      LikePostStatus likePostStatus,
      List<Tag> tags
  ) {
    return new LikePostResponseDto(post, likePostStatus, tags);

  }

}
