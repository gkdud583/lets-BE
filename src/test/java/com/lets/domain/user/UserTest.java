package com.lets.domain.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lets.domain.post.Post;
import com.lets.security.AuthProvider;

public class UserTest {
  @Test
  @DisplayName("createUser메서드는 유저를 생성한다")
  void createUser() {
    //given

    //when
    User user = User.createUser("user1", "123", AuthProvider.google, "default");

    //then
    assertThat(user.getNickname()).isEqualTo("user1");
  }

  @Test
  @DisplayName("changePublicId메서드는 유저의 publicId를 변경한다")
  void changePublicId() {
    //given
    String publicId = "default";
    User user = User.createUser("user1", "123", AuthProvider.google, "default");

    //when
    user.changePublicId(publicId);

    //then
    assertThat(user.getPublicId()).isEqualTo(publicId);
  }

  @Test
  @DisplayName("changeNickname메서드는 유저의 닉네임을 변경한다")
  void changeNickname() {
    //given
    String nickname = "newName";
    User user = User.createUser("user1", "123", AuthProvider.google, "default");

    //when
    user.changeNickname(nickname);

    //then
    assertThat(user.getNickname()).isEqualTo(nickname);
  }

  @Test
  @DisplayName("isWriterOf메서드는 사용자가 글의 작성자가 맞는지 확인한다")
  void isWriterOf() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    Post post = Post.createPost(user, "title", "content");

    //when
    boolean isWriter = user.isWriterOf(post);

    //then
    assertThat(isWriter).isEqualTo(true);
  }
}
