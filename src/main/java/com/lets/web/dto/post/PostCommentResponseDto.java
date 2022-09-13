package com.lets.web.dto.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lets.domain.comment.Comment;
import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostStatus;
import com.lets.domain.tag.Tag;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.comment.CommentResponseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PostCommentResponseDto {
  private String profile;

  private Long id;

  private Long likeCount;

  private Long viewCount;

  private List<String> tags = new ArrayList<>();

  private PostStatus status;

  private String title;

  private String content;

  private LikePostStatus likePostStatus;

  private LocalDateTime createdDate;

  private List<CommentResponseDto> comments = new ArrayList<>();

  private String nickname;

  public PostCommentResponseDto(
      Post post,
      LikePostStatus likePostStatus,
      List<Tag> tags,
      List<Comment> comments,
      String profile,
      CloudinaryUtil cloudinaryUtil,
      String nickName
  ) {
    this.profile = profile;
    this.id = post.getId();
    this.title = post.getTitle();
    this.content = post.getContent();
    this.likeCount = post.getLikeCount();
    this.viewCount = post.getViewCount();
    this.status = post.getStatus();
    this.likePostStatus = likePostStatus;
    this.createdDate = post.getCreatedDate();
    this.tags = tags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());
    this.comments = makeComment(comments, cloudinaryUtil);
    this.nickname = nickName;
  }

  public static PostCommentResponseDto PostToDto(
      Post post,
      LikePostStatus likePostStatus,
      List<Tag> tags,
      List<Comment> comments,
      String profile,
      CloudinaryUtil cloudinaryUtil,
      String nickName
  ) {
    return new PostCommentResponseDto(post, likePostStatus, tags, comments, profile, cloudinaryUtil,
                                      nickName
    );
  }

  private List<CommentResponseDto> makeComment(
      List<Comment> comments,
      CloudinaryUtil cloudinaryUtil
  ) {
    List<CommentResponseDto> commentList = new ArrayList<>();

    for (Comment comment : comments) {
      commentList.add(CommentResponseDto.from(
          comment,
          cloudinaryUtil.findFileURL(comment
                                         .getUser()
                                         .getPublicId())
      ));
    }

    return commentList;
  }
}
