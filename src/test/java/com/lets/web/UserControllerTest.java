package com.lets.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpStatus.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.exception.ErrorResponse;
import com.lets.security.AuthProvider;
import com.lets.security.JwtAuthentication;
import com.lets.security.JwtTokenProvider;
import com.lets.security.UserPrincipal;
import com.lets.service.likePost.LikePostService;
import com.lets.service.post.PostService;
import com.lets.service.postTechStack.PostTechStackService;
import com.lets.service.tag.TagService;
import com.lets.service.user.UserService;
import com.lets.util.CloudinaryUtil;
import com.lets.util.CookieUtil;
import com.lets.util.RedisUtil;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.user.SettingRequestDto;
import com.lets.web.dto.user.SettingResponseDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class UserControllerTest {
  @LocalServerPort
  private int port;

  @Autowired
  TagService tagService;

  @Autowired
  TagRepository tagRepository;

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PostService postService;

  @Autowired
  PostRepository postRepository;

  @Autowired
  PostTechStackService postTechStackService;

  @Autowired
  PostTechStackRepository postTechStackRepository;

  @Autowired
  LikePostService likePostService;

  @Autowired
  LikePostRepository likePostRepository;

  @Autowired
  CloudinaryUtil cloudinaryUtil;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  CookieUtil cookieUtil;

  @Autowired
  RedisUtil redisUtil;

  @SpyBean
  private JwtTokenProvider jwtTokenProvider;

  private User user;
  private UserPrincipal principal;
  private Authentication authentication;
  private String accessToken = "Bearer ";
  private String refreshToken;
  private Cookie refreshTokenCookie;

  @BeforeEach
  void before() {
    user = User.createUser("user1", "1234", AuthProvider.google, "default");

    userRepository.save(user);

    principal = UserPrincipal.create(user);

    authentication = new JwtAuthentication(principal);
    accessToken += jwtTokenProvider.generateRefreshToken(authentication);
    refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);

  }

  @AfterEach
  void after() {
    likePostRepository.deleteAllInBatch();
    postTechStackRepository.deleteAllInBatch();
    tagRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    redisUtil.deleteData(refreshToken);
  }

  //security test
  @Test
  @DisplayName("accessToken이 없을 경우 401 을 반환합니다")
  void myPostsWithNonexistentAccessToken() {
    //given
    HttpHeaders headers = new HttpHeaders();
    String url = "http://localhost:" + port + "/api/users/myPosts";

    //when
    ResponseEntity<Object> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                           new HttpEntity<>(headers), Object.class
    );

    //then
    //ACCESS_TOKEN_NOT_FOUND(UNAUTHORIZED, "요청 헤더에 ACCESS_TOKEN이 존재하지 않습니다."),
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

  }

  @Test
  @DisplayName("refreshToken이 없을 경우 400 을 반환합니다")
  void myPostsWithNonexistentRefreshToken() {
    //given
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    String url = "http://localhost:" + port + "/api/users/myPosts";
    given(jwtTokenProvider.validateToken(any()))
        .willReturn(false);

    //when
    ResponseEntity<Object> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                           new HttpEntity<>(headers), Object.class
    );

    //then
    //REFRESH_TOKEN_NOT_FOUND(BAD_REQUEST, "쿠키에 REFRESH_TOKEN이 존재하지 않습니다."),
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Test
  @DisplayName("refresh token이 유효하지 않을 경우 401 을 반환합니다")
  void myPostsWithInvalidRefreshToken1() {
    //given
    principal = UserPrincipal.create(1L, user);
    authentication = new JwtAuthentication(principal);
    refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
    redisUtil.setData(refreshToken, Long.toString(2L));

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    headers.add("Cookie", "refreshToken=" + refreshTokenCookie.getValue());

    given(jwtTokenProvider.validateToken(any()))
        .willReturn(false);

    String url = "http://localhost:" + port + "/api/users/myPosts";

    //when
    ResponseEntity<Object> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                           new HttpEntity<>(headers), Object.class
    );

    //then
    //INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 REFRESH_TOKEN이 유효하지 않습니다."),
    assertThat(res.getStatusCode()).isEqualTo(UNAUTHORIZED);

  }

  @Test
  @DisplayName("refresh token이 유효하지 않을 경우 401 을 반환합니다")
  void myPosts_유효_하지_않은_refresh_token2() {
    //given
    principal = UserPrincipal.create(1L, user);
    authentication = new JwtAuthentication(principal);
    refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
    redisUtil.setData(refreshToken, Long.toString(1L));

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    headers.add("Cookie", "refreshToken=" + refreshTokenCookie.getValue());

    given(jwtTokenProvider.validateToken(any()))
        .willReturn(false);

    String url = "http://localhost:" + port + "/api/users/myPosts";

    //when
    ResponseEntity<Object> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                           new HttpEntity<>(headers), Object.class
    );

    //then
    //INVALID_REFRESH_TOKEN(UNAUTHORIZED, "REFRESH_TOKEN이 유효하지 않습니다."),
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

  }

  @Test
  @DisplayName("findMyPosts메서드는 유저가 존재하지 않을 경우 404를 반환한다")
  void myPostsWithNonexistentUser() {
    //given
    userRepository.deleteById(user.getId());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/users/myPosts";

    //when
    ResponseEntity<ErrorResponse> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                  new HttpEntity<>(headers),
                                                                  ErrorResponse.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res
                   .getBody()
                   .getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("findMyPosts메서드는 유저가 작성한 게시글을 반환한다")
  void myPosts() {
    //given
    Post post = Post.createPost(user, "title1", "content1");
    postRepository.save(post);

    Tag tag = Tag.createTag("spring");
    tagRepository.save(tag);

    PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
    postTechStackService.save(postTechStack);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    String url = "http://localhost:" + port + "/api/users/myPosts";

    //when
    ResponseEntity<List<PostResponseDto>> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                          new HttpEntity<>(headers),
                                                                          new ParameterizedTypeReference<List<PostResponseDto>>() {
                                                                          }
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res
                   .getBody()
                   .size()).isEqualTo(1);

  }

  @Test
  @DisplayName("findMyLikes메서드는 유저가 존재하지 않을 경우 404를 반환한다")
  void myLikesWithNonexistentUser() {
    //given
    userRepository.deleteById(user.getId());
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/users/myLikes";

    //when
    ResponseEntity<ErrorResponse> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                  new HttpEntity<>(headers),
                                                                  ErrorResponse.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res
                   .getBody()
                   .getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("findMyLikes메서드는 유저가 조회한 게시글을 반환한다")
  void myLikes() {
    //given
    Post post = Post.createPost(user, "title1", "content1");
    postRepository.save(post);

    Tag tag = Tag.createTag("spring");
    tagRepository.save(tag);

    PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
    postTechStackService.save(postTechStack);

    LikePost likePost = LikePost.createLikePost(user, post);
    likePostRepository.save(likePost);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    String url = "http://localhost:" + port + "/api/users/myLikes";

    //when
    ResponseEntity<List<PostResponseDto>> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                          new HttpEntity<>(headers),
                                                                          new ParameterizedTypeReference<List<PostResponseDto>>() {
                                                                          }
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res
                   .getBody()
                   .size()).isEqualTo(1);

  }

  @Test
  @DisplayName("getSetting메서드는 유저의 설정 정보를 반환한다")
  void getSetting() {
    //given
    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<SettingResponseDto> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                       new HttpEntity<>(headers),
                                                                       SettingResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res
                   .getBody()
                   .getNickname()).isEqualTo(user.getNickname());
    assertThat(res
                   .getBody()
                   .getProfile()
                   .contains("default"));

  }

  @Test
  @DisplayName("getSetting메서드는 존재하지 않는 유저일 경우 404를 반환한다")
  void getSettingWithNonexistentUser() {
    //given
    userRepository.deleteById(user.getId());
    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<ErrorResponse> res = testRestTemplate.exchange(url, HttpMethod.GET,
                                                                  new HttpEntity<>(headers),
                                                                  ErrorResponse.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res
                   .getBody()
                   .getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("setSetting메서드는 존재하지 않는 유저일 경우 404를 반환한다")
  void setSettingWithNonexistentUser() {
    //given
    userRepository.deleteById(user.getId());

    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    SettingRequestDto settingRequestDto = new SettingRequestDto("default", user.getNickname(),
                                                                new ArrayList<>()
    );

    //when
    ResponseEntity<ErrorResponse> res = testRestTemplate.exchange(url, HttpMethod.PATCH,
                                                                  new HttpEntity<>(
                                                                      settingRequestDto, headers),
                                                                  ErrorResponse.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res
                   .getBody()
                   .getMessage()).isEqualTo("해당 유저 정보를 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("setSetting메서드는 유저의 설정 정보를 변경한다")
  void setSetting1() {

    //given
    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<SettingResponseDto> res = testRestTemplate.exchange(url, HttpMethod.PATCH,
                                                                       new HttpEntity<>(
                                                                           new SettingRequestDto(
                                                                               "KEEP", "user2",
                                                                               Arrays.asList()
                                                                           ),
                                                                           headers
                                                                       ),
                                                                       SettingResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userService
                   .findById(user.getId())
                   .getNickname()).isEqualTo("user2");
    assertThat(userService
                   .findById(user.getId())
                   .getPublicId()).isEqualTo("default");

  }

  @Test
  @DisplayName("setSetting메서드는 유저의 설정 정보를 변경한다")
  void setSetting2() {

    //given
    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<SettingResponseDto> res = testRestTemplate.exchange(url, HttpMethod.PATCH,
                                                                       new HttpEntity<>(
                                                                           new SettingRequestDto(
                                                                               "PUBLIC", "user2",
                                                                               Arrays.asList()
                                                                           ),
                                                                           headers
                                                                       ),
                                                                       SettingResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userService
                   .findById(user.getId())
                   .getNickname()).isEqualTo("user2");
    assertThat(userService
                   .findById(user.getId())
                   .getPublicId()).isEqualTo("default");

  }

  @Test
  @DisplayName("setSetting메서드는 유저의 설정 정보를 변경한다")
  void setSetting3() {

    //given
    File file = new File("src/test/java/com/lets/tea.jpg");

    String encodedImage = null;
    try {
      FileInputStream fis = new FileInputStream(file);
      encodedImage = Base64.encodeBase64String(fis.readAllBytes());
    } catch (Exception e) {
      throw new RuntimeException();
    }

    String url = "http://localhost:" + port + "/api/users/setting";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<SettingResponseDto> res = testRestTemplate.exchange(url, HttpMethod.PATCH,
                                                                       new HttpEntity<>(
                                                                           new SettingRequestDto(
                                                                               encodedImage,
                                                                               "user2",
                                                                               Arrays.asList()
                                                                           ),
                                                                           headers
                                                                       ),
                                                                       SettingResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(userService
                   .findById(user.getId())
                   .getNickname()).isEqualTo("user2");
    assertThat(userService
                   .findById(user.getId())
                   .getPublicId()).isNotEqualTo("default");

  }
}
