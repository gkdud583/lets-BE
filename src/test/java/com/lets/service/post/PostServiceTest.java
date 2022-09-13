package com.lets.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.comment.CommentRepository;
import com.lets.domain.likePost.LikePost;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.exception.CustomException;
import com.lets.security.AuthProvider;
import com.lets.service.user.UserService;
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

  User user = User.createUser("user1", "123", AuthProvider.google, "user");
  Post post = Post.createPost(user, "title1", "content1");
  List<Post> posts = Arrays.asList(post);
  Tag tag = Tag.createTag("spring");
  PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);
  List<String> tags = Arrays.asList("spring");
  List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));
  long commentCount = 0;

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
    given(userService.findById(anyLong()))
        .willReturn(user);
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
}
