package com.lets.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.data.domain.Sort.Direction.*;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.security.AuthProvider;
import com.lets.service.user.UserService;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.comment.CommentSearchRequestDto;
import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.PostCommentResponseDto;
import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostRecommendResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.post.PostSaveRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;
import com.lets.web.dto.post.PostUpdateRequestDto;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
  @InjectMocks
  PostService postService;

  @Mock
  UserService userService;

  @Mock
  PostRepository postRepository;

  @Mock
  LikePostRepository likePostRepository;

  @Mock
  PostTechStackRepository postTechStackRepository;

  @Mock
  CommentRepository commentRepository;

  @Mock
  CloudinaryUtil cloudinaryUtil;

  @Mock
  TagRepository tagRepository;

  static long userId = 1l;
  static long postId = 1l;

  static long commentId = 1;;
  static User user = User.createUser("user1", "123", AuthProvider.google, "user");
  static Post post = Post.createPost(user, "title1", "content1");
  static List<Post> posts = Arrays.asList(post);
  static Tag tag = Tag.createTag("spring");
  static PostTechStack postTechStack = PostTechStack.createPostTechStack(tag, post);
  static List<PostTechStack> postTechStacks = Arrays.asList(postTechStack);

  static List<Comment> comments = Arrays.asList(Comment.createComment(user, post, "test"));

  static List<String> tags = Arrays.asList("spring");
  static List<LikePost> likePosts = Arrays.asList(LikePost.createLikePost(user, post));
  static long commentCount = 0;

  @BeforeAll
  static void setup() {
    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(post, "id", postId);
    ReflectionTestUtils.setField(comments.get(0), "id", commentId);
  }
  @Test
  @DisplayName("findOneById메서드는 아이디로 글을 조회한다")
  void findOneById() {
    // given
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    // when
    Post foundPost = postService.findOneById(postId);

    // then
    Assertions
        .assertThat(foundPost)
        .isEqualTo(post);
  }

  @Test
  @DisplayName("findOneById메서드는 존재하지 않는 아이디라면 예외를 던진다")
  void findOneByIdWithNonexistentId() {
    // given
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> {
      postService.findOneById(postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findUserPosts메서드는 유저가 작성한 글을 조회한다")
  void findPosts() {
    //given
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
    List<PostResponseDto> result = postService.findUserPosts(userId);

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

  @Test
  @DisplayName("changePostStatus메서드는 존재하지 않는 유저라면 예외를 던진다")
  void changePostStatusWithNonexistentUser() {
    //given
    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    //when, then
    assertThatThrownBy(() -> {
      postService.changePostStatus(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("changePostStatus메서드는 존재하지 않는 글이라면 예외를 던진다")
  void changePostStatusWithNonexistentPost() {
    //given
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postRepository.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

    //when, then
    assertThatThrownBy(() -> {
      postService.changePostStatus(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findById메서드는 로그인하지 않은 유저라면 아이디로 글을 조회한다")
  void findByIdWithNotUser() {
    //given
    String profile = "profile";

    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    given(postTechStackRepository.findAllByPosts(anyList()))
        .willReturn(postTechStacks);
    given(commentRepository.findComments(any(CommentSearchRequestDto.class)))
        .willReturn(comments);


    //when
    PostCommentResponseDto result = postService.findById(null, postId);

    //then
    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result.getId()).isEqualTo(postId);
    assertThat(result.getNickname()).isEqualTo(user.getNickname());
    assertThat(result.getCreatedDate()).isEqualTo(post.getCreatedDate());
    assertThat(result.getContent()).isEqualTo(post.getContent());
    assertThat(result.getStatus()).isEqualTo(post.getStatus());
    assertThat(result.getTags().size()).isEqualTo(tags.size());
    assertThat(result.getComments().size()).isEqualTo(comments.size());
    assertThat(result.getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result.getViewCount()).isEqualTo(1);
    assertThat(result.getLikePostStatus()).isEqualTo(LikePostStatus.INACTIVE);
  }

  @Test
  @DisplayName("findById메서드는 로그인한 유저라면 아이디로 글을 조회하고 조회 수와 조회한 글 목록을 수정한다")
  void findByIdWithUser() {
    //given
    String profile = "profile";

    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    given(postTechStackRepository.findAllByPosts(anyList()))
        .willReturn(postTechStacks);
    given(likePostRepository.findByUserIdAndPostId(anyLong(), anyLong()))
        .willReturn(Optional.of(likePosts.get(0)));
    given(commentRepository.findComments(any(CommentSearchRequestDto.class)))
        .willReturn(comments);


    //when
    PostCommentResponseDto result = postService.findById(userId, postId);

    //then
    verify(userService).findById(anyLong());
    verify(likePostRepository).findByUserIdAndPostId(anyLong(), anyLong());

    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result.getId()).isEqualTo(postId);
    assertThat(result.getNickname()).isEqualTo(user.getNickname());
    assertThat(result.getCreatedDate()).isEqualTo(post.getCreatedDate());
    assertThat(result.getContent()).isEqualTo(post.getContent());
    assertThat(result.getStatus()).isEqualTo(post.getStatus());
    assertThat(result.getTags().size()).isEqualTo(tags.size());
    assertThat(result.getComments().size()).isEqualTo(comments.size());
    assertThat(result.getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result.getViewCount()).isEqualTo(1);
    assertThat(result.getLikePostStatus()).isEqualTo(LikePostStatus.INACTIVE);
  }

  @Test
  @DisplayName("findById메서드는 존재하지 않는 유저라면 예외를 던진다")
  void findByIdWithNonexistentUser() {
    //given
    String profile = "profile";

    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    given(postTechStackRepository.findAllByPosts(anyList()))
        .willReturn(postTechStacks);
    given(commentRepository.findComments(any(CommentSearchRequestDto.class)))
        .willReturn(comments);
    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));


    //when, then
    assertThatThrownBy(() -> {
      postService.findById(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("findById메서드는 존재하지 않는 글이라면 예외를 던진다")
  void findByIdWithNonexistentPost() {
    //given
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when, then
    assertThatThrownBy(() -> {
      postService.findById(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("changeLikeStatus메서드는 좋아요 상태를 변경하고 좋아요 수를 수정한다")
  void changeLikeStatus() {
    //given
    given(likePostRepository.findByUserIdAndPostId(anyLong(), anyLong()))
        .willReturn(Optional.of(likePosts.get(0)));

    //when
    ChangeLikePostStatusResponseDto result = postService.changeLikeStatus(
        userId,
        postId
    );

    //then
    assertThat(result.getLikeCount()).isEqualTo(1);
    assertThat(result.getLikePostStatus()).isEqualTo(LikePostStatus.ACTIVE);
  }

  @Test
  @DisplayName("changeLikeStatus메서드는 조회한 적 없는 글이라면 예외를 던진다")
  void changeLikeStatusWithNeverSeenPost() {
    //given
    given(likePostRepository.findByUserIdAndPostId(anyLong(), anyLong()))
        .willReturn(Optional.empty());

    //when, then
    assertThatThrownBy(() -> {
      postService.changeLikeStatus(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");

  }

  @Test
  @DisplayName("searchPosts메서드는 조건에 맞는 게시글을 조회한다")
  void searchPosts() {
    //given
    String profile = "profile";
    given(postTechStackRepository.findPostTechStacks(any(PostSearchRequestDto.class), any(Pageable.class)))
        .willReturn(postTechStacks);
    given(commentRepository.countByPost(any(Post.class)))
        .willReturn(commentCount);
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    PostSearchRequestDto postSearchRequestDto = new PostSearchRequestDto(
        PostStatus.RECRUITING.name(),
        tags
    );

    PageRequest pageRequest = PageRequest.of(0, 20, DESC, "createdDate");

    //when
    List<PostResponseDto> result = postService.searchPosts(
        postSearchRequestDto,
        pageRequest
    );

    //then
    assertThat(result.get(0).getContent()).isEqualTo(post.getContent());
    assertThat(result.get(0).getId()).isEqualTo(postId);
    assertThat(result.get(0).getTitle()).isEqualTo(post.getTitle());
    assertThat(result.get(0).getViewCount()).isEqualTo(post.getViewCount());
    assertThat(result.get(0).getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result.get(0).getTags().size()).isEqualTo(1);
    assertThat(result.get(0).getProfile()).isEqualTo(profile);
    assertThat(result.get(0).getCommentCount()).isEqualTo(commentCount);
    assertThat(result.get(0).getStatus()).isEqualTo(post.getStatus());
  }

  @Test
  @DisplayName("savePost메서드는 게시글을 저장한다")
  void savePost() {
    //given
    String title = "title";
    String content = "content";
    String profile = "profile";
    long postId = 1l;
    Post savedPost = Post.createPost(user, title, content);

    given(userService.findById(anyLong()))
        .willReturn(user);
    given(tagRepository.findAllByNameIn(tags))
        .willReturn(List.of(tag));
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);
    given(postRepository.save(any(Post.class)))
        .willReturn(savedPost);
    ReflectionTestUtils.setField(savedPost, "id", postId);
    //when
    PostResponseDto result = postService.savePost(
        userId,
        new PostSaveRequestDto(title,
                               content,
                               tags
        )
    );
    //then
    assertThat(result.getStatus()).isEqualTo(PostStatus.RECRUITING);
    assertThat(result.getTitle()).isEqualTo(title);
    assertThat(result.getViewCount()).isEqualTo(savedPost.getViewCount());
    assertThat(result.getLikeCount()).isEqualTo(savedPost.getLikeCount());
    assertThat(result.getTags().size()).isEqualTo(tags.size());
    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result.getCommentCount()).isEqualTo(0);
    assertThat(result.getContent()).isEqualTo(savedPost.getContent());
    assertThat(result.getId()).isEqualTo(postId);
  }

  @Test
  @DisplayName("savePost메서드는 존재하지 않는 유저라면 예외를 던진다")
  void savePostWithNonexistentUser() {
    //given
    String title = "title";
    String content = "content";

    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    //when, then
    assertThatThrownBy(() -> {
      postService.savePost(userId, new PostSaveRequestDto(title,
                                                          content,
                                                          tags
      ));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("updatePost메서드는 게시글을 수정한다")
  void updatePost() {
    //given
    String title = "title1";
    String content = "content1";
    Tag tag = Tag.createTag("java");
    List<String> tags = List.of("java");

    String profile = "profile";
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));
    given(tagRepository.findAllByNameIn(anyList()))
        .willReturn(List.of(tag));
    given(commentRepository.countByPost(any(Post.class)))
        .willReturn(commentCount);
    given(cloudinaryUtil.findFileURL(anyString()))
        .willReturn(profile);

    //when
    PostResponseDto result = postService.updatePost(
        userId,
        postId,
        new PostUpdateRequestDto(title,
                                 content,
                                 tags
        )
    );

    //then
    assertThat(result.getId()).isEqualTo(postId);
    assertThat(result.getViewCount()).isEqualTo(post.getViewCount());
    assertThat(result.getLikeCount()).isEqualTo(post.getLikeCount());
    assertThat(result.getCommentCount()).isEqualTo(commentCount);
    assertThat(result.getTags().get(0)).isEqualTo(tags.get(0));
    assertThat(result.getProfile()).isEqualTo(profile);
    assertThat(result.getStatus()).isEqualTo(post.getStatus());
    assertThat(result.getTitle()).isEqualTo(title);
    assertThat(result.getContent()).isEqualTo(content);



  }

  @Test
  @DisplayName("updatePost메서드는 유저가 존재하지 않다면 예외를 던진다")
  void updatePostWithNonexistentUser() {
    //given
    String title = "title1";
    String content = "content1";
    List<String> tags = List.of("java");

    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    //when, then
    assertThatThrownBy(() -> {
      postService.updatePost(userId, postId, new PostUpdateRequestDto(
          title,
          content,
          tags
      ));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("updatePost메서드는 게시글이 존재하지 않다면 예외를 던진다")
  void updatePostWithNonexistentPost() {
    //given
    String title = "title1";
    String content = "content1";
    List<String> tags = List.of("java");

    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when, then
    assertThatThrownBy(() -> {
      postService.updatePost(userId, postId, new PostUpdateRequestDto(
          title,
          content,
          tags
      ));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("updatePost메서드는 유저가 작성자가 아니라면 예외를 던진다")
  void updatePostWithNotWriter() {
    //given
    long userId = 2l;
    User newUser = User.createUser("nickname", "11213", AuthProvider.google, "default");
    ReflectionTestUtils.setField(newUser, "id", userId);
    String title = "title1";
    String content = "content1";
    List<String> tags = List.of("java");

    given(userService.findById(anyLong()))
        .willReturn(newUser);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    //when, then
    assertThatThrownBy(() -> {
      postService.updatePost(userId, postId, new PostUpdateRequestDto(
          title,
          content,
          tags
      ));
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("접근 권한이 없습니다.");
  }

  @Test
  @DisplayName("deletePost메서드는 게시글을 삭제한다")
  void deletePost() {
    //given
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    //when
    postService.deletePost(userId, postId);

    //then
    verify(postTechStackRepository).deleteAllByPost(anyList());
    verify(likePostRepository).deleteAllByPost(anyList());
    verify(commentRepository).deleteAllByPost(any(Post.class));
    verify(postRepository).delete(any(Post.class));
  }

  @Test
  @DisplayName("deletePost메서드는 존재하지 않는 유저라면 예외를 던진다")
  void deletePostNonexistentUser() {
    //given
    given(userService.findById(anyLong()))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    //when, then
    assertThatThrownBy(() -> {
      postService.deletePost(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 유저 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("deletePost메서드는 게시글이 존재하지 않는다면 예외를 던진다")
  void deletePostNonexistentPost() {
    //given
    given(userService.findById(anyLong()))
        .willReturn(user);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when, then
    assertThatThrownBy(() -> {
      postService.deletePost(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("해당 게시글을 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("deletePost메서드는 유저가 작성자가 아니라면 예외를 던진다")
  void deletePostNotWriter() {
    //given
    long userId = 2l;
    User newUser = User.createUser("nickname", "11213", AuthProvider.google, "default");
    ReflectionTestUtils.setField(newUser, "id", userId);

    given(userService.findById(anyLong()))
        .willReturn(newUser);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    //when, then
    assertThatThrownBy(() -> {
      postService.deletePost(userId, postId);
    })
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("접근 권한이 없습니다.");
  }

  @Test
  @DisplayName("recommendPosts메서드는 게시글을 추천합니다")
  void recommendPosts() {
    //given
    given(postTechStackRepository.findRecommendPosts(any(PostRecommendRequestDto.class), anyLong(), anyLong()))
        .willReturn(postTechStacks);
    //when
    List<PostRecommendResponseDto> result = postService.recommendPosts(
        userId,
        postId,
        new PostRecommendRequestDto(tags)
    );

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getId()).isEqualTo(postId);
    assertThat(result.get(0).getTitle()).isEqualTo(post.getTitle());
  }
}
