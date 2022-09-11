package com.lets.domain.postTechStack;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.lets.domain.post.Post;
import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.security.AuthProvider;

public class PostTechStackTest {
    @Test
    public void createPostTechStack(){
        //given
        Tag tag = Tag.createTag("tag1");
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        Post post = Post.createPost(user, "title1", "content1");

        //when
        PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);

        //then
        assertThat(postTechStack.getTag().getName()).isEqualTo("tag1");
    }

    @Test
    public void setPost(){
        //given
        Tag tag = Tag.createTag("tag1");
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        Post post = Post.createPost(user, "title1", "content1");

        PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, null);

        //when
        postTechStack.setPost(post);
        //then
        assertThat(postTechStack.getTag().getName()).isEqualTo("tag1");
    }
}
