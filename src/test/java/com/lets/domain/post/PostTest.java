package com.lets.domain.post;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lets.domain.comment.Comment;
import com.lets.domain.user.User;
import com.lets.security.AuthProvider;

public class PostTest {
  @Test
  @DisplayName("createPost메서드는 게시글을 생성한다")
  void createPost() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");

    //when
    Post post = Post.createPost(user, "title1", "content1");

    //then
    assertThat(post.getContent()).isEqualTo("content1");
  }

  @Test
  @DisplayName("addComment메서드는 글에 댓글을 추가한다")
  void addComment() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");
    Comment comment = Comment.createComment(user, post, "comment1"); //size = 1

    //when
    post.addComment(comment); //size = 2

    //then
    assertThat(post
                   .getComments()
                   .size()).isEqualTo(2);
  }

  @Test
  @DisplayName("addLike메서드는 글에 좋아요를 추가한다")
  void addLike() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");

    //when
    post.addLike();

    //then
    assertThat(post.getLikeCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("minusLike메서드는 글에 좋아요를 뺀다")
  void minusLike() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");
    post.addLike();

    //when
    post.minusLike();

    //then
    assertThat(post.getLikeCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("addView메서드는 글에 조회수를 더한다")
  void addView() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");

    //when
    post.addView();

    //then
    assertThat(post.getViewCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("change메서드는 글의 제목과 내용을 변경한다")
  void change() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");

    //when
    post.change("title2", "content2");

    //then
    assertThat(post.getContent()).isEqualTo("content2");
  }

  @Test
  @DisplayName("changeStatus메서드는 글의 상태를 변경한다")
  void changeStatus() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");

    //when
    post.changeStatus();

    //then
    assertThat(post.getStatus()).isEqualTo(PostStatus.COMPLETE);
  }
}
