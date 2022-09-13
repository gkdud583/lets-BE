package com.lets.domain.post;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.lets.domain.BaseTimeEntity;
import com.lets.domain.comment.Comment;
import com.lets.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id")
  private Long id;

  private Long likeCount;

  private Long viewCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<Comment> comments = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private PostStatus status;

  @NotBlank
  private String title;

  @Lob
  @NotBlank
  private String content;

  private Post(
      User user,
      String title,
      String content
  ) {
    this.user = user;
    this.title = title;
    this.content = content;
    this.likeCount = 0L;
    this.viewCount = 0L;
    status = PostStatus.RECRUITING;
  }

  //==연관관계 메서드==//
  public void addComment(Comment comment) {
    comments.add(comment);
  }

  //==생성 메서드==//
  public static Post createPost(
      User user,
      String title,
      String content
  ) {
    Post post = new Post(user, title, content);
    return post;
  }

  //==좋아요 클릭==//
  public void addLike() {
    this.likeCount++;
  }

  //==좋아요 취소==//
  public void minusLike() {
    this.likeCount--;
  }

  //==조회수 증가==//
  public void addView() {
    this.viewCount++;
  }

  //==필드 변경==//
  public Post change(
      String title,
      String content
  ) {
    this.title = title;
    this.content = content;
    return this;
  }

  @Override
  public String toString() {
    return "Post{" +
        "id=" + id +
        ", likeCount=" + likeCount +
        ", viewCount=" + viewCount +
        ", user=" + user +
        ", comments=" + comments +
        ", status=" + status +
        ", title='" + title + '\'' +
        ", content='" + content + '\'' +
        '}';
  }
}
