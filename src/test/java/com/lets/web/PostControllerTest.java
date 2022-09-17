package com.lets.web;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

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
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.post.PostStatus;
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
import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.ChangePostStatusResponseDto;
import com.lets.web.dto.post.PostRecommendResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.post.PostSaveRequestDto;
import com.lets.web.dto.post.PostUpdateRequestDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class PostControllerTest {
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

  private Tag tag;
  private UserPrincipal principal;
  private Authentication authentication;
  private String accessToken = "Bearer ";
  private String refreshToken;
  private Cookie refreshTokenCookie;

  @BeforeEach
  void before() {
    user = User.createUser("user", "123", AuthProvider.google, "default");
    userRepository.save(user);

    tag = Tag.createTag("spring");
    tagRepository.save(tag);

    post = Post.createPost(user, "title1", "content1");

    postRepository.save(post);

    commentRepository.save(Comment.createComment(user, post, "content1"));

    PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);

    postTechStackService.save(postTechStack);

    principal = UserPrincipal.create(user);

    authentication = new JwtAuthentication(principal);
    accessToken += jwtTokenProvider.generateAccessToken(authentication);
    refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
    refreshTokenCookie = cookieUtil.createCookie("refreshToken", refreshToken);

  }

  @AfterEach
  void after() {
    commentRepository.deleteAllInBatch();
    postTechStackRepository.deleteAllInBatch();
    likePostRepository.deleteAllInBatch();
    postRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    tagRepository.deleteAllInBatch();

  }

  @Test
  @DisplayName("findPost메서드는 로그인한 유저가 아니라면 아이디로 포스트를 조회한다")
  void findPostWithNotUser() {
    // given
    String url = "http://localhost:" + port + "/api/posts/" + post.getId();
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        PostResponseDto.class
    );

    assertThat(res
                   .getBody()
                   .getId()).isEqualTo(post.getId());
    assertThat(res
                   .getBody()
                   .getViewCount()).isEqualTo(0);
    Optional<LikePost> likePost = likePostRepository.findByUserIdAndPostId(
        user.getId(),
        post.getId()
    );
    assertThat(likePost).isEmpty();
  }

  @Test
  @DisplayName("findPost메서드는 유저가 존재한다면 아이디로 포스트를 조회하고 조회한 포스트로 추가한다")
  void findPost() {
    // given
    long expectedViewCount = post.getViewCount() + 1;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        PostResponseDto.class
    );

    assertThat(res
                   .getBody()
                   .getId()).isEqualTo(post.getId());
    assertThat(res
                   .getBody()
                   .getViewCount()).isEqualTo(expectedViewCount);
    Optional<LikePost> likePost = likePostRepository.findByUserIdAndPostId(
        user.getId(),
        post.getId()
    );
    assertThat(likePost).isNotEmpty();
  }
  @Test
  @DisplayName("findPost메서드는 존재하지 않는 아이디라면 404를 반환한다")
  void findPostWithNonexistentPost() {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    long postId = -1;
    String url = "http://localhost:" + port + "/api/posts/" + postId;
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        PostResponseDto.class
    );

    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("findPost메서드는 존재하지 않는 유저라면 404를 반환한다")
  void findPostWithNonexistentUser() {
    // given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    userRepository.delete(newUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        PostResponseDto.class
    );

    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("savePost메서드는 포스트 한 건을 저장한다")
  void savePost() throws Exception {
    String title = "title444";
    String content = "content";
    List<String> tags = List.of("spring");

    //LogIn
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //post등록
    String url = "http://localhost:" + port + "/api/posts";

    RequestEntity<PostSaveRequestDto> body = RequestEntity
        .post(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostSaveRequestDto(title, content, tags));

    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        body,
        PostResponseDto.class
    );

    assertThat(res.getBody().getTags().size()).isEqualTo(tags.size());
    assertThat(res.getBody().getTitle()).isEqualTo(title);
    assertThat(res.getBody().getContent()).isEqualTo(content);
    assertThat(res.getBody().getViewCount()).isEqualTo(0);
    assertThat(res.getBody().getLikeCount()).isEqualTo(0);
    assertThat(res.getBody().getCommentCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("savePost메서드는 존재하지 않는 유저라면 404를 반환한다")
  void savePostWithNonexistentUser() throws Exception {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    userRepository.delete(newUser);
    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    String title = "title444";
    String content = "content";
    List<String> tags = List.of("spring");

    //LogIn
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization",  accessToken);

    String url = "http://localhost:" + port + "/api/posts";

    //when
    RequestEntity<PostSaveRequestDto> body = RequestEntity
        .post(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostSaveRequestDto(title, content, tags));

    ResponseEntity<Object> res = testRestTemplate.exchange(body, Object.class);

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("updatePost메서드는 포스트를 수정한다")
  void updatePost() throws Exception {
    String title = "title44";
    String content = "content44";
    List<String> tags = List.of("spring");

    //LogIn
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //post등록
    String url = "http://localhost:" + port + "/api/posts/" + post.getId();

    RequestEntity<PostUpdateRequestDto> body = RequestEntity
        .put(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostUpdateRequestDto(title, content, tags));
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        body,
        PostResponseDto.class
    );

    assertThat(res
                   .getBody()
                   .getTitle()).isEqualTo(title);
    assertThat(res.getBody().getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("updatePost메서드는 존재하지 않는 유저라면 404를 반환한다")
  void updatePostWithNonexistentUser() throws Exception {
    //given
    String title = "title44";
    String content = "content44";
    List<String> tags = List.of("spring");

    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    userRepository.delete(newUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();

    RequestEntity<PostUpdateRequestDto> body = RequestEntity
        .put(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostUpdateRequestDto(title, content, tags));

    //when
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        body,
        PostResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("updatePost메서드는 존재하지 않는 글이라면 404를 반환한다")
  void updatePostWithNonexistentPost() throws Exception {
    //given
    long postId = -1l;

    String title = "title44";
    String content = "content44";
    List<String> tags = List.of("spring");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + postId;

    RequestEntity<PostUpdateRequestDto> body = RequestEntity
        .put(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostUpdateRequestDto(title, content, tags));

    //when
    ResponseEntity<PostResponseDto> res = testRestTemplate.exchange(
        body,
        PostResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }
  @Test
  @DisplayName("updatePost메서드는 수정권한이 없는 유저라면 401을 반환한다")
  void updatePostWithUnauthorizedUser() throws Exception {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    String title = "title44";
    String content = "content44";
    List<String> tags = List.of("spring");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization",  accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();
    RequestEntity<PostUpdateRequestDto> body = RequestEntity
        .put(new URI(url))
        .accept(MediaType.APPLICATION_JSON)
        .headers(headers)
        .body(new PostUpdateRequestDto(title, content, tags));

    //when
    ResponseEntity<Object> res = testRestTemplate.exchange(body, Object.class);

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("deletePost메서드는 존재하지 않는 유저라면 404를 반환한다")
  void deletePostWithNonexistentUser() {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    userRepository.delete(newUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("deletePost메서드는 존재하지 않는 글이라면 404를 반환한다")
  void deletePostWithNonexistentPost() {
    //given
    long postId = -1;

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + postId;

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }
  @Test
  @DisplayName("deletePost메서드는 포스트를 삭제한다")
  void deletePost() {
    //given
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);
    headers.add("contentType", "application/json");

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("deletePost메서드는 삭제 권한이 없는 유저라면 401을 반환한다")
  void deletePostWithUnauthorizedUser() {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization",  accessToken);
    headers.add("contentType", "application/json");

    String url = "http://localhost:" + port + "/api/posts/" + post.getId();

    //when
    ResponseEntity<ApiResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.DELETE,
        new HttpEntity<>(headers),
        ApiResponseDto.class
    );
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("changeLikeStatus메서드는 좋아요 상태를 변경한다")
  void changeLikeStatus() throws URISyntaxException {
    //given
    LikePost likePost = LikePost.createLikePost(user, post);
    likePostRepository.save(likePost);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);


    //when
    String url = "http://localhost:" + port + "/api/posts/" + post.getId() + "/likes";

    RequestEntity<Long> body = RequestEntity
        .post(new URI(url))
        .headers(headers)
        .body(null);

    ResponseEntity<ChangeLikePostStatusResponseDto> res = testRestTemplate.exchange(
        body,
        ChangeLikePostStatusResponseDto.class
    );

    //then
    assertThat(res
                   .getBody()
                   .getLikeCount()).isEqualTo(1);
    assertThat(res
                   .getBody()
                   .getLikePostStatus()).isEqualTo(LikePostStatus.ACTIVE);
  }

  @Test
  @DisplayName("changeLikeStatus메서드는 조회한적 없는 글이라면 404를 반환한다")
  void changeLikeStatusWithNeverSeenPost() throws URISyntaxException {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    Post newPost = Post.createPost(newUser, "title", "content");
    postRepository.save(newPost);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);


    //when
    String url = "http://localhost:" + port + "/api/posts/" + newPost.getId() + "/likes";

    RequestEntity<Long> body = RequestEntity
        .post(new URI(url))
        .headers(headers)
        .body(null);

    ResponseEntity<ChangeLikePostStatusResponseDto> res = testRestTemplate.exchange(
        body,
        ChangeLikePostStatusResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("searchPosts메서드는 조건에 맞는 글을 검색한다")
  void searchPosts() {
    //given
    String url = "http://localhost:" + port
        + "/api/posts/filter?status=RECRUITING&page=0&sort=createdDate,DESC&tags=spring,java";

    //when
    ResponseEntity<List<PostResponseDto>> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res
                   .getBody()
                   .size()).isEqualTo(1);
  }

  @Test
  @DisplayName("recommendPosts메서드는 추천 포스트 목록을 생성한다")
  void recommendPosts() {
    //given
    String url = "http://localhost:" + port + "/api/posts/2/recommends?tags=java,c,python";
    //LogIn
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<List<PostRecommendResponseDto>> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res
                   .getBody()
                   .size()).isEqualTo(1);
  }

  @Test
  @DisplayName("changePostStatus메서드는 모집 상태를 변경한다")
  void changePostStatus() {
    //given
    String url = "http://localhost:" + port + "/api/posts/" + post.getId() + "/status";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<ChangePostStatusResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.POST,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res
                   .getBody()
                   .getStatus()).isEqualTo(PostStatus.COMPLETE);
  }

  @Test
  @DisplayName("changePostStatus메서드는 존재하지 않는 글이라면 404를 반환한다")
  void changePostStatusWithNonexistentPost() {
    //given
    long postId = -1;
    String url = "http://localhost:" + port + "/api/posts/" + postId + "/status";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);


    //when
    ResponseEntity<ChangePostStatusResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.POST,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("changePostStatus메서드는 존재하지 않는 유저라면 404를 반환한다")
  void changePostStatusWithNonexistentUser() {
    //given
    User newUser = User.createUser("newUser", "123443", AuthProvider.google, "default");
    userRepository.save(newUser);

    principal = UserPrincipal.create(newUser);

    authentication = new JwtAuthentication(principal);
    accessToken = "Bearer " + jwtTokenProvider.generateAccessToken(authentication);

    userRepository.delete(newUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization",  accessToken);

    String url = "http://localhost:" + port + "/api/posts/" + post.getId() + "/status";

    //when
    ResponseEntity<ChangePostStatusResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.POST,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  @DisplayName("changePostStatus메서드는 유저가 작성자가 아니라면 401를 반환한다")
  void changePostStatusWithNotWriter() {
    //given
    User newUser = User.createUser("newUser", "123456", AuthProvider.google, "default");
    userRepository.save(newUser);

    Post post = Post.createPost(newUser, "title", "content");
    postRepository.save(post);

    String url = "http://localhost:" + port + "/api/posts/+ " + post.getId() + "/status";
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", accessToken);

    //when
    ResponseEntity<ChangePostStatusResponseDto> res = testRestTemplate.exchange(
        url,
        HttpMethod.POST,
        new HttpEntity<>(headers),
        ChangePostStatusResponseDto.class
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
