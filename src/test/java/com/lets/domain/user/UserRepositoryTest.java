package com.lets.domain.user;

import com.lets.config.QueryDslConfig;
import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @DisplayName("id로 유저 단건 조회합니다.")
    @Test
    public void findById() {
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        //when
        Optional<User> findUser = userRepository.findById(user.getId());

        //then
        assertThat(findUser.get().getNickname()).isEqualTo("user1");
    }

    @DisplayName("socialLoginId & authProvider로 유저 단건 조회합니다.")
    @Test
    public void findBySocialLoginIdAndAuthProvider() {
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        //when
        Optional<User> findUser = userRepository.findBySocialLoginIdAndAuthProvider(user.getSocialLoginId(), user.getAuthProvider());

        //then
        assertThat(findUser.get().getNickname()).isEqualTo("user1");
    }

    @DisplayName("닉네임으로 유저가 존재하는지 확인합니다.")
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

    @DisplayName("socialLoginId & authProvider으로 유저가 존재하는지 확인합니다.")
    @Test
    public void existsBySocialLoginIdAndAuthProvider() {
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        //when
        Boolean result = userRepository.existsBySocialLoginIdAndAuthProvider(user.getSocialLoginId(), user.getAuthProvider());

        //then
        assertThat(result).isTrue();
    }

}
