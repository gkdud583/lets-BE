package com.lets.service.likepost;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.likePost.LikePostStatus;
import com.lets.domain.post.Post;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.security.AuthProvider;
import com.lets.service.likePost.LikePostService;
import com.lets.service.user.UserService;
import com.lets.web.dto.likepost.LikePostResponseDto;

@ExtendWith(MockitoExtension.class)
public class LikePostServiceTest {
  @InjectMocks
  LikePostService likePostService;

  @Mock
  UserService userService;

  @Mock
  PostTechStackRepository postTechStackRepository;

  @Mock
  LikePostRepository likePostRepository;

  User user = User.createUser("user1", "123", AuthProvider.google, null);
  Post post = Post.createPost(user, "title1", "content1");
  Tag tag = Tag.createTag("spring");
  PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);
  List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));

  @Test
  @DisplayName("findLikePosts메서드는 유저가 조회 한 글을 조회한다")
  void findLikePosts() {
    //given
    long userId = 1l;
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(likePostRepository.findAllByUser(any()))
        .willReturn(likePosts);
    given(postTechStackRepository.findAllByPosts(anyList()))
        .willReturn(postTechStacks);

    //when
    List<LikePostResponseDto> result = likePostService.findLikePosts(userId);

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result
                   .get(0)
                   .getTitle()).isEqualTo(post.getTitle());
    assertThat(result
                   .get(0)
                   .getContent()).isEqualTo(post.getContent());
    assertThat(result
                   .get(0)
                   .getLikePostStatus()).isEqualTo(LikePostStatus.INACTIVE);
    assertThat(result
                   .get(0)
                   .getViewCount()).isEqualTo(post.getViewCount());
    assertThat(result
                   .get(0)
                   .getTags()
                   .size()).isEqualTo(1);
  }
}
