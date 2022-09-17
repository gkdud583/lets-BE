package com.lets.domain.postTechStack;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.lets.config.QueryDslConfig;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.post.PostStatus;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.AuthProvider;
import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;

@DataJpaTest
@Import(QueryDslConfig.class)
public class PostTechStackRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private PostTechStackRepository postTechStackRepository;

  private User user;
  private Post post;
  private Tag tag;
  private PostTechStack postTechStack;

  @BeforeEach
  void setup() {
    user = User.createUser("user1", "123", AuthProvider.google, "default");
    userRepository.save(user);

    post = Post.createPost(user, "title1", "content1");
    postRepository.save(post);

    tag = Tag.createTag("spring");
    tagRepository.save(tag);

    postTechStack = PostTechStack.createPostTechStack(tag, post);
    postTechStackRepository.save(postTechStack);
  }

  @AfterEach
  void afterTest() {
    postTechStackRepository.deleteAll();
    tagRepository.deleteAll();
    postRepository.deleteAll();
    userRepository.deleteAll();
  }

  @DisplayName("deleteAllByPost메서드는 글의 모든 글 기술 스택을 삭제한다")
  @Test
  public void deleteAllByPost() {
    //given
    //when
    postTechStackRepository.deleteAllByPost(Arrays.asList(post));

    //then
    assertThat(postTechStackRepository.count()).isEqualTo(0);
  }

  @DisplayName("findAllByPosts메서드는 글의 모든 글 기술 스택을 조회한다")
  @Test
  public void findAllByPosts() {
    //given
    //when
    List<PostTechStack> result = postTechStackRepository.findAllByPosts(Arrays.asList(post));

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getId()).isEqualTo(postTechStack.getId());

  }

  @DisplayName("findAllByUser메서드는 유저의 모든 글 기술 스택을 조회한다")
  @Test
  public void findAllByUser() {
    //given
    //when
    List<PostTechStack> result = postTechStackRepository.findAllByUser(user);

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getId()).isEqualTo(postTechStack.getId());
  }

  @DisplayName("findPostTechStacks메서드는 검색 조건으로 포스트를 조회한다")
  @Test
  public void findPostTechStacks() {
    //given
    setupPost(40);
    PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("createdDate"));
    PostSearchRequestDto postSearchRequestDto = new PostSearchRequestDto(
        PostStatus.RECRUITING.name(), Arrays.asList(tag.getName()));

    //when
    List<PostTechStack> postTechStacks = postTechStackRepository.findPostTechStacks(
        postSearchRequestDto, pageRequest);

    //then
    assertThat(postTechStacks.size()).isEqualTo(20);
  }

  @DisplayName("findRecommendedPosts메서드는 추천 포스트를 조회한다")
  @Test
  public void findRecommendedPosts() {
    //given
    setupPost(4);
    PostRecommendRequestDto postRecommendRequestDto = new PostRecommendRequestDto(
        Arrays.asList(tag.getName()));

    //when
    List<PostTechStack> recommendedPosts = postTechStackRepository.findRecommendPosts(
        postRecommendRequestDto, user.getId(), post.getId());

    //then
    assertThat(recommendedPosts.size()).isEqualTo(4);
  }

  private Post setupPost(int size) {
    Post lastPost = null;
    User user2 = User.createUser("user2", "123", AuthProvider.google, "default");
    userRepository.save(user2);

    for (int i = 1; i <= size; i++) {
      Post post = Post.createPost(user2, "title" + i, "content" + i);
      postRepository.save(post);
      lastPost = post;

      PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
      postTechStackRepository.save(postTechStack);
    }
    return lastPost;

  }
}
