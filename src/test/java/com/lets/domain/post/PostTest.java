package com.lets.domain.post;

import com.lets.domain.comment.Comment;
import com.lets.domain.user.User;
import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {
    @Test
    public void createPost(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        //when
        Post post = Post.createPost(user, "title1", "content1");

        //then
        assertThat(post.getContent()).isEqualTo("content1");
    }

    @Test
    public void addComment(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");
        Comment comment = Comment.createComment(user, post, "comment1"); //size = 1

        //when
        post.addComment(comment); //size = 2

        //then
        assertThat(post.getComments().size()).isEqualTo(2);
    }

    @Test
    public void addLike(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");

        //when
        post.addLike();

        //then
        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    public void minusLike(){
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
    public void addView(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");

        //when
        post.addView();

        //then
        assertThat(post.getViewCount()).isEqualTo(1);
    }

    @Test
    public void change(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");

        //when
        post.change("title2", "content2");

        //then
        assertThat(post.getContent()).isEqualTo("content2");
    }
}
