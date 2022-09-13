package com.lets.web.dto.post;

import java.util.List;

import com.lets.domain.comment.Comment;
import com.lets.domain.post.PostStatus;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
  private String title;
  private String content;
  private User user;
  private List<PostTechStack> postTechStacks;
  private List<Comment> comments;
  private PostStatus status;
  private Long likeCount;
  private Long viewCount;

  public PostRequestDto(
      String title,
      String content,
      User user,
      List<PostTechStack> postTechStacks,
      List<Comment> comments,
      PostStatus status,
      Long likeCount,
      Long viewCount
  ) {
    this.title = title;
    this.content = content;
    this.user = user;
    this.postTechStacks = postTechStacks;
    this.comments = comments;
    this.status = status;
    this.likeCount = likeCount;
    this.viewCount = viewCount;
  }
}
