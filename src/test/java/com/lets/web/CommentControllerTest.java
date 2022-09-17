package com.lets.web;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.AuthProvider;
import com.lets.security.JwtAuthentication;
import com.lets.security.JwtTokenProvider;
import com.lets.security.UserPrincipal;
import com.lets.service.likePost.LikePostService;
import com.lets.service.post.PostService;
import com.lets.service.postTechStack.PostTechStackService;
import com.lets.service.tag.TagService;
import com.lets.service.user.UserService;
import com.lets.util.CookieUtil;
import com.lets.util.RedisUtil;
import com.lets.web.dto.ApiResponseDto;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {
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
  CommentRepository commentRepository;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  CookieUtil cookieUtil;

  @Autowired
  RedisUtil redisUtil;

  @SpyBean
  private JwtTokenProvider jwtTokenProvider;


  private User user;

  private Post post;
  private UserPrincipal principal;
  private Authentication authentication;
  private String accessToken = "Bearer ";
  private String refreshToken;
  private Cookie refreshTokenCookie;

  @BeforeEach
  void before() {
    user = User.createUser("user2", "123", AuthProvider.google, null);
    userRepository.save(user);

    Tag tag1 = Tag.createTag("spring");
    Tag tag2 = Tag.createTag("java");

    tagRepository.save(tag1);
    tagRepository.save(tag2);

    post = Post.createPost(user, "title1", "content1");
    Post post2 = Post.createPost(user, "title2", "content2");

    postRepository.save(post);
    postRepository.save(post2);

    Comment comment = Comment.createComment(user, post, "content333");
    commentRepository.save(comment);

    PostTechStack postTechStack1 = PostTechStack.createPostTechStack(tag1, post);
    PostTechStack postTechStack2 = PostTechStack.createPostTechStack(tag2, post2);

    postTechStackService.save(postTechStack1);
    postTechStackService.save(postTechStack2);

    principal = UserPrincipal.create(user);

    authentication = new JwtAuthentication(principal);
    accessToken += jwtTokenProvider.generateRefreshToken(authentication);
    refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);
  }

  @AfterEach
  void after() {
    postTechStackRepository.deleteAllInBatch();
    commentRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    tagRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("save메서드는 댓글을 저장한다")
  void save() throws URISyntaxException {
    Long postId = postRepository
        .findAll()
        .get(0)
        .getId();

    //LogIn
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //post등록
    String url = "http://localhost:" + port + "/api/posts/" + postId + "/comments";

    RequestEntity<CommentSaveRequestDto> body = RequestEntity
        .post(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new CommentSaveRequestDto("content333"));

    ResponseEntity<CommentResponseDto> res = testRestTemplate.exchange(
        body,
        CommentResponseDto.class
    );

    assertThat(res
                   .getBody()
                   .getContent()).isEqualTo("content333");
  }

  @Test
  @DisplayName("save메서드는 존재하지 않는 유저라면 404를 반환한다")
  void saveWithNonexistentUser() throws URISyntaxException {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    userRepository.delete(newUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" +  post.getId() + "/comments";

    RequestEntity<CommentSaveRequestDto> body = RequestEntity
        .post(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new CommentSaveRequestDto("content333"));

    //when
    ResponseEntity<CommentResponseDto> res = testRestTemplate.exchange(
        body,
        CommentResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("save메서드는 존재하지 않는 게시글이라면 404를 반환한다")
  void saveWithNonexistentPost() throws URISyntaxException {
    //given
    long postId = -1;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" +  postId + "/comments";

    RequestEntity<CommentSaveRequestDto> body = RequestEntity
        .post(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new CommentSaveRequestDto("content333"));

    //when
    ResponseEntity<CommentResponseDto> res = testRestTemplate.exchange(
        body,
        CommentResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("update메서드는 댓글 내용을 수정한다")
  void update() throws URISyntaxException {
    //given
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    Long commentId = commentRepository
        .findAll()
        .get(0)
        .getId();

    String updateUrl =
        "http://localhost:" + port + "/api/posts/" + post.getId() + "/comments/" + commentId;
    RequestEntity<CommentUpdateRequestDto> updateBody = RequestEntity
        .put(new URI(updateUrl))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new CommentUpdateRequestDto("content222"));
    //when
    ResponseEntity<CommentResponseDto> updateRes = testRestTemplate.exchange(
        updateBody,
        CommentResponseDto.class
    );

    //then
    assertThat(updateRes
                   .getBody()
                   .getContent()).isEqualTo("content222");
  }

  @Test
  @DisplayName("update메서드는 존재하지 않는 댓글이라면 404를 던진다")
  void updateWithNonexistentComment() throws URISyntaxException {
    //given
    long commentId = 1l;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String updateUrl =
        "http://localhost:" + port + "/api/posts/" + post.getId() + "/comments/" + commentId;
    RequestEntity<CommentUpdateRequestDto> updateBody = RequestEntity
        .put(new URI(updateUrl))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new CommentUpdateRequestDto("content222"));
    //when
    ResponseEntity<CommentResponseDto> updateRes = testRestTemplate.exchange(
        updateBody,
        CommentResponseDto.class
    );

    //then
    assertThat(updateRes.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("delete메서드는 댓글을 삭제한다")
  void delete() {
    //given
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    headers.add("contentType", "application/json");
    Long commentId = commentRepository
        .findAll()
        .get(0)
        .getId();
    String deleteUrl =
        "http://localhost:" + port + "/api/posts/" + post.getId() + "/comments/" + commentId;

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        deleteUrl,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("delete메서드는 존재하지 않는 댓글이라면 404를 던진다")
  void deleteWithNonexistentComment() {
    //given
    long commentId = -1;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    headers.add("contentType", "application/json");

    String deleteUrl =
        "http://localhost:" + port + "/api/posts/" + post.getId() + "/comments/" + commentId;

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        deleteUrl,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
