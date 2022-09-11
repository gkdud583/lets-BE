package com.lets.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
import com.lets.domain.user.UserRepository;
import com.lets.security.AuthProvider;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.post.PostSaveRequestDto;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
  @InjectMocks
  PostService postService;

  @Mock
  CloudinaryUtil cloudinaryUtil;

  @Mock
  TagRepository tagRepository;

  @Mock
  PostRepository postRepository;

  @Mock
  PostTechStackRepository postTechStackRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  LikePostRepository likePostRepository;

  User user = User.createUser("user1", "123", AuthProvider.google, "user");
  Post post = Post.createPost(user, "title1", "content1");
  List<Post> posts = Arrays.asList(post);
  Tag tag = Tag.createTag("spring");
  PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);
  List<String> tags = Arrays.asList("spring");
  List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));

  @Test
  @Transactional
  void 단일게시글_조회() {
    given(postRepository.findOneById(any())).willReturn(Optional.of(post));

    assertThat(postRepository
                   .findOneById(any())
                   .get()).isEqualTo(post);
  }

  @Test
  void searchPosts() {
    given(postTechStackRepository.findAllByUser(any())).willReturn(null);

    assertThat(postTechStackRepository.findAllByUser(any())).isEqualTo(null);

  }

  @Test
  void findPosts() {
    //when
    given(postService.findPosts(any())).willReturn(null);

    //then
    assertThat(postService.findPosts(any())).isEqualTo(null);
  }

  @Test
  @Transactional
  void savePost() {
    PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto();
    postSaveRequestDto.setContent("11");
    postSaveRequestDto.setTitle("11");

    PostResponseDto postResponseDto = postService.savePost(user, postSaveRequestDto);

    assertThat(postResponseDto.getTitle()).isEqualTo("11");
  }

}
