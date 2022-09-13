package com.lets.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.exception.CustomException;
import com.lets.security.AuthProvider;
import com.lets.web.dto.comment.auth.SignupRequestDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;


    private SignupRequestDto signupRequestDto = new SignupRequestDto(null, "user1", "1234", AuthProvider.google, new ArrayList<>());




    @Test
    void validateName_실패(){
        //given
        given(userRepository.existsByNickname(any()))
                .willReturn(true);
        //when
        Exception exception  = Assertions.assertThrows(CustomException.class, () -> userService.validateNickname(any()));

        //then
        assertEquals("중복된 닉네임입니다.", exception.getMessage());


    }
    @Test
    void validateName_성공(){
        //given
        given(userRepository.existsByNickname(any()))
                .willReturn(false);
        //when
        userService.validateNickname(any());
        //then


    }


    @Test
    void findBySocialLoginIdAndAuthProvider_성공(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "123");
        given(userRepository.findBySocialLoginIdAndAuthProvider(any(), any()))
                .willReturn(Optional.of(user));
        //when
        User findUser = userService.findBySocialLoginIdAndAuthProvider(any(), any());

        //then
        assertThat(findUser).isNotNull();
    }
    @Test
    void findBySocialLoginIdAndAuthProvider_실패(){
        //given
        given(userRepository.findBySocialLoginIdAndAuthProvider(any(), any()))
                .willReturn(Optional.ofNullable(null));
        //when
        Exception exception  = Assertions.assertThrows(CustomException.class, () -> userService.findBySocialLoginIdAndAuthProvider(any(), any()));

        //then
        assertThat(exception.getMessage()).isEqualTo("로그인 정보[SOCIAL_LOGIN_ID, AUTH_PROVIDER]가 올바르지 않습니다.");


    }

    @Test
    void existsById_성공() {
        //given
        given(userRepository.existsById(any()))
                .willReturn(true);
        //when
        boolean result = userService.existsById(any());

        //then
        assertThat(result).isTrue();
    }
    @Test
    void existsById_실패() {
        //given
        given(userRepository.existsById(any()))
                .willReturn(false);
        //when
        Exception exception  = Assertions.assertThrows(CustomException.class, () -> userService.existsById(any()));

        //then
        assertThat(exception.getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

    }
    @Test
    void findOneById_성공(){
        //given
        User user = User.createUser("user1", "123", AuthProvider.google, "123");
        given(userRepository.findById(any()))
                .willReturn(Optional.of(user));
        //when
        User findUser = userService.findById(user.getId());

        //then
        assertThat(findUser).isNotNull();

    }
    @Test
    void findOneById_실패(){
        //given
        given(userRepository.findById(any()))
                .willReturn(Optional.ofNullable(null));
        //when
        Exception exception  = Assertions.assertThrows(CustomException.class, () -> userService.findById(any()));

        //then
        assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getMessage());

    }


}
