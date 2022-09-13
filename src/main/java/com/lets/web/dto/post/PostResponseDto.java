package com.lets.web.dto.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lets.domain.post.Post;
import com.lets.domain.post.PostStatus;
import com.lets.domain.tag.Tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 글 검색 응답에 사용하는 DTO
 * 글 검색시 LikePostStatus는 필요없으므로 추가하지 않았음.
 */
@Getter
@NoArgsConstructor
@ToString
public class PostResponseDto {
  private String profile;

  private Long id;

  private Long likeCount;

  private Long viewCount;

  private List<String> tags = new ArrayList<>();

  private PostStatus status;

  private String title;

  private String content;

  private Long commentCount;

  public PostResponseDto(
      Post post,
      List<Tag> tags,
      String profile,
      Long commentCount
  ) {
    this.profile = profile;
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.likeCount = post.getLikeCount();
    this.viewCount = post.getViewCount();
    this.status = post.getStatus();
    this.tags = tags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());
    this.commentCount = commentCount;
  }

  public static PostResponseDto PostToDto(
      Post post,
      List<Tag> tags,
      String profile,
      Long commentCount
  ) {
    return new PostResponseDto(post, tags, profile, commentCount);

  }

}
