package com.lets.domain.likePost;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.lets.domain.BaseTimeEntity;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LikePost extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "like_post_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  @Enumerated(EnumType.STRING)
  private LikePostStatus status;

  private LikePost(
      User user,
      Post post
  ) {
    this.user = user;
    this.post = post;
    status = LikePostStatus.INACTIVE;
  }

  public static LikePost createLikePost(
      User user,
      Post post
  ) {
    LikePost likePost = new LikePost(user, post);

    return likePost;
  }

  public void changeLikeStatus() {
    if (this.getStatus() == LikePostStatus.ACTIVE) {
      deactivateLike();
    } else {
      activateLike();
    }
  }

  //==좋아요 메서드==//
  private void activateLike() {
    this.status = LikePostStatus.ACTIVE;
    post.addLike();
  }

  //==좋아요 취소 메서드==//
  private void deactivateLike() {
    this.status = LikePostStatus.INACTIVE;
    post.minusLike();
  }
}
