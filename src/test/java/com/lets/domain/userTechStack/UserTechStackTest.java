package com.lets.domain.userTechStack;

import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTechStackTest {
    @Test
    public void createUserTechStack(){
        //given
        Tag tag = Tag.createTag("tag1");
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        //when
        UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);

        //then
        assertThat(userTechStack.getUser().getNickname()).isEqualTo("user1");
    }

    @Test
    public void setUser(){
        //given
        Tag tag = Tag.createTag("tag1");
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, null);

        //when
        userTechStack.setUser(user);

        //then
        assertThat(userTechStack.getUser().getNickname()).isEqualTo("user1");
    }
}
