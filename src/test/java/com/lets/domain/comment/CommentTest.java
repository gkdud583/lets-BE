package com.lets.domain.comment;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import com.lets.security.oauth2.AuthProvider;

public class CommentTest {

  @Test
  public void createComment() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");

    //when
    Comment comment = Comment.createComment(user, post, "comment1");
    //then
    assertThat(comment.getContent()).isEqualTo("comment1");
    assertThat(post
                   .getComments()
                   .size()).isEqualTo(1);

  }

  @Test
  public void change() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title1", "content1");
    Comment comment = Comment.createComment(user, post, "comment1");

    //when
    comment.change("comment2");

    //then
    assertThat(comment.getContent()).isEqualTo("comment2");
  }

}
