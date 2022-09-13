package com.lets.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.domain.userTechStack.UserTechStack;
import com.lets.domain.userTechStack.UserTechStackRepository;
import com.lets.exception.CustomException;
import com.lets.security.AuthProvider;
import com.lets.util.CloudinaryUtil;
import com.lets.util.FileUtil;
import com.lets.web.dto.auth.SignupRequestDto;
import com.lets.web.dto.user.SettingRequestDto;
import com.lets.web.dto.user.SettingResponseDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  UserTechStackRepository userTechStackRepository;

  @Mock
  TagRepository tagRepository;

  @Mock
  CloudinaryUtil cloudinaryUtil;

  @Mock
  FileUtil fileUtil;

  SignupRequestDto signupRequestDto = new SignupRequestDto(
      null,
      "user1",
      "1234",
      AuthProvider.google,
      new ArrayList<>()
  );
  User user = User.createUser("nickname", "1234", AuthProvider.google, "PUBLIC");

  Tag tag = Tag.createTag("spring");

  UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);

  @Test
  @DisplayName("validateName메서드는 이름이 중복이라면 예외를 던진다")
  void validateNameWithDuplicateName() {
    //given
    given(userRepository.existsByNickname(any()))
        .willReturn(true);
    //when
    Exception exception = Assertions.assertThrows(
        CustomException.class,
        () -> userService.validateNickname(any())
    );

    //then
    assertEquals("중복된 닉네임입니다.", exception.getMessage());

  }

  @Test
  @DisplayName("validateName메서드는 이름이 중복인지 확인한다")
  void validateName() {
    //given
    given(userRepository.existsByNickname(any()))
        .willReturn(false);
    //when
    userService.validateNickname(any());
    //then

  }

  @Test
  @DisplayName("findBySocialLoginIdAndAuthProvider메서드는 socialLoginId & authProvider로 유저를 조회한다")
  void findBySocialLoginIdAndAuthProvider() {
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
  @DisplayName("findBySocialLoginIdAndAuthProvider메서드는 유저가 존재하지 않는다면 예외를 던진다")
  void findBySocialLoginIdAndAuthProviderWithNonexistentUser() {
    //given
    given(userRepository.findBySocialLoginIdAndAuthProvider(any(), any()))
        .willReturn(Optional.ofNullable(null));
    //when
    Exception exception = Assertions.assertThrows(
        CustomException.class,
        () -> userService.findBySocialLoginIdAndAuthProvider(
            any(),
            any()
        )
    );

    //then
    assertThat(exception.getMessage()).isEqualTo(
        "로그인 정보[SOCIAL_LOGIN_ID, AUTH_PROVIDER]가 올바르지 않습니다.");

  }

  @Test
  @DisplayName("existsById메서드는 아이디로 유저가 존재하는지 확인한다")
  void existsById() {
    //given
    given(userRepository.existsById(any()))
        .willReturn(true);
    //when
    boolean result = userService.existsById(any());

    //then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("existsById메서드는 아이디로 유저가 존재하지 않는다면 예외를 던진다")
  void existsByIdWithNonexistentUser() {
    //given
    given(userRepository.existsById(any()))
        .willReturn(false);
    //when
    Exception exception = Assertions.assertThrows(
        CustomException.class,
        () -> userService.existsById(any())
    );

    //then
    assertThat(exception.getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("findById메서드는 아이디로 유저를 조회한다")
  void findById() {
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
  @DisplayName("findById메서드는 유저가 존재하지 않는다면 예외를 던진다")
  void findByIdWithNonexistentUser() {
    //given
    given(userRepository.findById(any()))
        .willReturn(Optional.ofNullable(null));
    //when
    Exception exception = Assertions.assertThrows(
        CustomException.class,
        () -> userService.findById(any())
    );

    //then
    assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getMessage());

  }

  @Test
  @DisplayName("getSetting메서드는 유저 설정 정보를 조회한다")
  void getSetting() {
    //given
    String profile = "profile";
    long userId = 1l;
    given(userRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(userTechStackRepository.findAllByUser(any(User.class)))
        .willReturn(List.of(userTechStack));
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);

    //when
    SettingResponseDto result = userService.getSetting(userId);

    //then
    assertThat(result.getNickname()).isEqualTo(user.getNickname());
    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result
                   .getTags()
                   .size()).isEqualTo(1);
  }

  @Test
  @DisplayName("setSetting메서드는 유저 설정 정보를 변경한다")
  void setSetting() {
    //given
    long userId = 1l;
    SettingRequestDto settingRequestDto = new SettingRequestDto(
        "PUBLIC",
        "newName",
        List.of(tag.getName())
    );
    String publicId = "default";
    String profile = "profile";

    given(userRepository.findById(anyLong()))
        .willReturn(Optional.of(user));
    given(tagRepository.findAllByNameIn(anyList()))
        .willReturn(List.of(tag));
    given(userTechStackRepository.saveAll(anyList()))
        .willReturn(List.of(userTechStack));
    given(userRepository.existsByNickname(anyString()))
        .willReturn(false);
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);

    //when
    SettingResponseDto result = userService.setSetting(userId, settingRequestDto);

    //then
    assertThat(result
                   .getTags()
                   .size()).isEqualTo(1);
    assertThat(result.getNickname()).isEqualTo(user.getNickname());
    assertThat(result.getProfile()).isEqualTo(profile);
  }
}
