package com.lets.web.dto.comment;

import java.time.LocalDateTime;

import com.lets.domain.comment.Comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {
  private String profile;
  private Long id;
  private String nickname;
  private String content;
  private LocalDateTime createdTime;

  private CommentResponseDto(
      Comment comment,
      String profile
  ) {
    this.profile = profile;
    this.id = comment.getId();
    this.nickname = comment
        .getUser()
        .getNickname();
    this.content = comment.getContent();
    this.createdTime = comment.getCreatedDate();
  }

  public static CommentResponseDto from(
      Comment comment,
      String profile
  ) {
    return new CommentResponseDto(comment, profile);
  }
}
