package com.lets.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.lets.domain.comment.CommentRepository;
import com.lets.domain.likePost.LikePost;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.post.PostStatus;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.security.AuthProvider;
import com.lets.service.user.UserService;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.post.PostResponseDto;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
  @InjectMocks
  PostService postService;

  @Mock
  UserService userService;

  @Mock
  PostRepository postRepository;

  @Mock
  PostTechStackRepository postTechStackRepository;

  @Mock
  CommentRepository commentRepository;

  @Mock
  CloudinaryUtil cloudinaryUtil;

  static long postId = 1l;
  static User user = User.createUser("user1", "123", AuthProvider.google, "user");
  static Post post = Post.createPost(user, "title1", "content1");
  static List<Post> posts = Arrays.asList(post);
  static Tag tag = Tag.createTag("spring");
  static PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  static List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);
  static List<String> tags = Arrays.asList("spring");
  static List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));
  static long commentCount = 0;

  @BeforeAll
  static void setup() {
    ReflectionTestUtils.setField(post, "id", postId);
  }
  @Test
  @DisplayName("findById메서드는 아이디로 글을 조회한다")
  void findById() {
    // given
    long id = 1l;
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    // when
    Post foundPost = postService.findById(id);

    // then
    Assertions
        .assertThat(foundPost)
        .isEqualTo(post);
  }

  @Test
  @DisplayName("findById메서드는 존재하지 않는 아이디라면 예외를 던진다")
  void findByIdWithNonexistentId() {
    // given
    long id = 1l;
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      postService.findById(id);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findPosts메서드는 유저가 작성한 글을 조회한다")
  void findPosts() {
    //given
    long userId = 1l;
    String profile = "default";
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    given(postTechStackRepository.findAllByUser(any(User.class)))
        .willReturn(postTechStacks);
    given(commentRepository.countByPost(any(Post.class)))
        .willReturn(commentCount);

    //when
    List<PostResponseDto> result = postService.findPosts(userId);

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getCommentCount()).isEqualTo(commentCount);
    assertThat(result
                   .get(0)
                   .getContent()).isEqualTo(post.getContent());
    assertThat(result
                   .get(0)
                   .getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result
                   .get(0)
                   .getStatus()).isEqualTo(post.getStatus());
    assertThat(result
                   .get(0)
                   .getTags()
                   .size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getTitle()).isEqualTo(post.getTitle());
    assertThat(result
                   .get(0)
                   .getViewCount()).isEqualTo(post.getViewCount());
  }

  @Test
  @DisplayName("changePostStatus메서드는 모집 상태를 변경한다")
  void changePostStatus() {
    //given
    long userId = 1l;
    long postId = 1l;
    User newUser = User.createUser("newUser", "12345", AuthProvider.google, "default");
    ReflectionTestUtils.setField(newUser, "id", userId);

    Post newPost = Post.createPost(newUser, "title", "content");

    given(userService.findById(anyLong()))
        .willReturn(newUser);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(newPost));


    //when
    PostStatus postStatus = postService.changePostStatus(userId, postId);

    //then
    assertThat(postStatus).isEqualTo(PostStatus.COMPLETE);
  }

  @Test
  @DisplayName("changePostStatus메서드는 유저가 작성자가 아니라면 예외를 던진다")
  void changePostStatusWithNotWriter() {
    //given
    long userId = 1l;
    long postId = 1l;
    User newUser1 = User.createUser("newUser", "12345", AuthProvider.google, "default");
    ReflectionTestUtils.setField(newUser1, "id", userId);

    Post newPost = Post.createPost(newUser1, "title", "content");

    User newUser2 = User.createUser("newUser2", "124566", AuthProvider.google, "default");

    given(userService.findById(anyLong()))
        .willReturn(newUser2);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(newPost));


    //when, then
    assertThatThrownBy(() -> {
      postService.changePostStatus(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("접근 권한이 없습니다.");
  }
}
