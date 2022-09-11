package com.lets.service.likepost;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.security.AuthProvider;
import com.lets.service.likePost.LikePostService;
import com.lets.web.dto.likepost.LikePostResponseDto;

@ExtendWith(MockitoExtension.class)
public class LikePostServiceTest {
  @InjectMocks
  LikePostService likePostService;
  @Mock
  TagRepository tagRepository;

  @Mock
  PostRepository postRepository;

  @Mock
  PostTechStackRepository postTechStackRepository;

  @Mock
  LikePostRepository likePostRepository;

  User user = User.createUser("user1", "123", AuthProvider.google, null);
  Post post = Post.createPost(user, "title1", "content1");
  List<Post> posts = Arrays.asList(post);
  Tag tag = Tag.createTag("spring");
  PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);
  List<String> tags = Arrays.asList("spring");
  List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));
  HashSet<Post> postSet = new HashSet<>();

  @Test
  void findLikePosts() {
    //given
    given(likePostRepository.findAllByUser(any()))
        .willReturn(likePosts);
    //when
    List<LikePostResponseDto> likePostResponseDtos = likePostService.findLikePosts(user);

    //then
    assertThat(likePostResponseDtos.size()).isEqualTo(1);
  }

  @Test
  void findLikePostsTags() {
    //given
    given(postTechStackRepository.findAllByPosts(anyList()))
        .willReturn(postTechStacks);
    //when
    List<LikePostResponseDto> likePostResponseDtos = likePostService.findLikePostsTags(
        likePosts,
        posts
    );

    //then
    assertThat(likePostResponseDtos.size()).isEqualTo(1);

  }

}
