package com.lets.domain.user;

import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    public void createUser(){
        //given

        //when
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        //then
        assertThat(user.getNickname()).isEqualTo("user1");
    }

    @Test
    public void change(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");

        //when
        user.change("123", "user2");
        //then
        assertThat(user.getNickname()).isEqualTo("user2");
    }
}
