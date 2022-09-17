package com.lets.domain.user;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lets.config.QueryDslConfig;
import com.lets.security.AuthProvider;

@DataJpaTest
@Import(QueryDslConfig.class)
public class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @DisplayName("findById메서드는 id로 유저 단건 조회한다")
  @Test
  public void findById() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    //when
    Optional<User> findUser = userRepository.findById(user.getId());

    //then
    assertThat(findUser
                   .get()
                   .getNickname()).isEqualTo("user1");
  }

  @DisplayName("findBySocialLoginIdAndAuthProvider메서드는 socialLoginId & authProvider로 유저 단건 조회한다")
  @Test
  public void findBySocialLoginIdAndAuthProvider() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    //when
    Optional<User> foundUser = userRepository.findBySocialLoginIdAndAuthProvider(
        user.getSocialLoginId(),
        user.getAuthProvider()
    );

    //then
    assertThat(foundUser.get().getId()).isEqualTo(user.getId());
  }

  @DisplayName("existsByNickname메서드는 닉네임으로 유저가 존재하는지 확인한다")
  @Test
  public void existsByNickname() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    //when
    Boolean result = userRepository.existsByNickname(user.getNickname());

    //then
    assertThat(result).isTrue();
  }

  @DisplayName("existsBySocialLoginIdAndAuthProvider메서드는 socialLoginId & authProvider으로 유저가 존재하는지 확인한다")
  @Test
  public void existsBySocialLoginIdAndAuthProvider() {
    //given
    User user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    //when
    Boolean result = userRepository.existsBySocialLoginIdAndAuthProvider(
        user.getSocialLoginId(),
        user.getAuthProvider()
    );

    //then
    assertThat(result).isTrue();
  }

}
