package com.lets.domain.likepost;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import com.lets.security.AuthProvider;

public class LikePostTest {

    @Test
    public void createLikePost(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");

        //when
        LikePost likePost = LikePost.createLikePost(user, post);

        //then
        assertThat(likePost.getPost().getContent()).isEqualTo("content1");
    }
    @Test
    public void changeLikeStatus(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        Post post = Post.createPost(user, "title1", "content1");

        LikePost likePost = LikePost.createLikePost(user, post);

        //when
        likePost.changeLikeStatus();

        //then
        assertThat(likePost.getStatus()).isEqualTo(LikePostStatus.ACTIVE);
        assertThat(post.getLikeCount()).isEqualTo(1);
    }
}
