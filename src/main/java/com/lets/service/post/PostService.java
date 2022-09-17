package com.lets.service.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.lets.service.user.UserService;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSearchRequestDto;
import com.lets.web.dto.likepost.ChangeLikePostStatusResponseDto;
import com.lets.web.dto.post.PostCommentResponseDto;
import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostRecommendResponseDto;
import com.lets.web.dto.post.PostResponseDto;
import com.lets.web.dto.post.PostSaveRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;
import com.lets.web.dto.post.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {
  private final LikePostRepository likePostRepository;
  private final PostTechStackRepository postTechStackRepository;
  private final PostRepository postRepository;

  private final UserService userService;

  private final TagRepository tagRepository;
  private final CloudinaryUtil cloudinaryUtil;
  private final CommentRepository commentRepository;

  public Post findOneById(long id) {
    return postRepository
        .findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
  }

  public List<PostResponseDto> searchPosts(
      PostSearchRequestDto search,
      Pageable pageable
  ) {

    //postTechStack 한번에 구해와서 애플리케이션에서 각 post 의 태그 정보 조립
    List<PostTechStack> postTechStacks = postTechStackRepository.findPostTechStacks(
        search,
        pageable
    );

    //각 post 의 태그 정보 조립
    return findPostsTags(postTechStacks);
  }

  public List<PostResponseDto> findUserPosts(long userId) {
    User user = userService.findById(userId);

    //postTechStack 한번에 구해와서 애플리케이션에서 각 post 의 태그 정보 조립
    List<PostTechStack> postTechStacks = postTechStackRepository.findAllByUser(user);

    if (postTechStacks == null) {
      return null;
    }

    //각 post 의 태그 정보 조립
    return findPostsTags(postTechStacks);
  }

  @Transactional
  public PostResponseDto savePost(
      long userId,
      PostSaveRequestDto postSaveRequestDto
  ) {
    User user = userService.findById(userId);
    Post post = Post.createPost(
        user,
        postSaveRequestDto.getTitle(),
        postSaveRequestDto.getContent()
    );
    post = postRepository.save(post);

    List<Tag> tags = tagRepository.findAllByNameIn(postSaveRequestDto.getTags());

    List<PostTechStack> postTechStacks = createPostTechStacks(tags, post);

    postTechStackRepository.saveAll(postTechStacks);

    String profile = cloudinaryUtil.findFileURL(user.getPublicId());

    List<String> tagNames = createTagNames(tags);

    return PostResponseDto.from(
        profile,
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getLikeCount(),
        post.getViewCount(),
        post.getStatus(),
        tagNames,
        0l
    );
  }

  @Transactional
  public PostResponseDto updatePost(
      long userId,
      long postId,
      PostUpdateRequestDto postUpdateRequestDto
  ) {
    User user = userService.findById(userId);
    Post post = findOneById(postId);

    if (!user.isWriterOf(post)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }

    post.change(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent());
    List<Tag> tags = tagRepository.findAllByNameIn(postUpdateRequestDto.getTags());

    postTechStackRepository.deleteAllByPost(Arrays.asList(post));

    List<PostTechStack> postTechStacks = createPostTechStacks(tags, post);

    String profile = cloudinaryUtil.findFileURL(user.getPublicId());
    postTechStackRepository.saveAll(postTechStacks);

    List<String> tagsNames = tags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());

    long commentCount = commentRepository.countByPost(post);

    return PostResponseDto.from(
        profile,
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getLikeCount(),
        post.getViewCount(),
        post.getStatus(),
        tagsNames,
        commentCount
    );
  }

  @Transactional
  public void deletePost(
      long userId,
      long postId
  ) {
    User user = userService.findById(userId);
    Post post = findOneById(postId);

    if (!user.isWriterOf(post)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }

    postTechStackRepository.deleteAllByPost(Arrays.asList(post));
    likePostRepository.deleteAllByPost(Collections.singletonList(post));
    commentRepository.deleteAllByPost(post);
    postRepository.delete(post);
  }

  @Transactional
  public PostCommentResponseDto findById(
      Long userId,
      long postId
  ) {
    Post post = findOneById(postId);

    String profile = cloudinaryUtil.findFileURL(post
                                                    .getUser()
                                                    .getPublicId());

    List<String> tags = postTechStackRepository
        .findAllByPosts(Collections.singletonList(
            post))
        .stream()
        .map(postTechStack -> postTechStack
            .getTag()
            .getName())
        .collect(
            Collectors.toList());

    List<CommentResponseDto> comments = commentRepository
        .findComments(new CommentSearchRequestDto(post))
        .stream()
        .map(comment -> CommentResponseDto.from(
            cloudinaryUtil.findFileURL(comment
                                           .getUser()
                                           .getPublicId()),
            comment.getId(),
            comment
                .getUser()
                .getNickname(),
            comment.getContent(),
            comment.getCreatedDate()
        ))
        .collect(Collectors.toList());

    if (userId == null) {
      return PostCommentResponseDto.from(
          profile,
          post.getId(),
          post.getLikeCount(),
          post.getViewCount(),
          tags,
          post.getStatus(),
          post.getTitle(),
          post.getContent(),
          LikePostStatus.INACTIVE,
          post.getCreatedDate(),
          comments,
          post
              .getUser()
              .getNickname()
      );
    }

    User user = userService.findById(userId);
    LikePost likePost = likePostRepository
        .findByUserIdAndPostId(userId, postId)
        .orElseGet(() -> {
          LikePost likePostCreate = LikePost.createLikePost(user, post);
          return likePostRepository.save(likePostCreate);
        });

    return PostCommentResponseDto.from(
        profile,
        post.getId(),
        post.getLikeCount(),
        post.getViewCount(),
        tags,
        post.getStatus(),
        post.getTitle(),
        post.getContent(),
        likePost.getStatus(),
        post.getCreatedDate(),
        comments,
        post
            .getUser()
            .getNickname()
    );
  }

  @Transactional
  public ChangeLikePostStatusResponseDto changeLikeStatus(
      long userId,
      long postId
  ) {
    LikePost likePost = likePostRepository
        .findByUserIdAndPostId(userId, postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    likePost.changeLikeStatus();

    return ChangeLikePostStatusResponseDto.of(likePost
                                                  .getPost()
                                                  .getLikeCount(), likePost.getStatus());
  }

  public List<PostRecommendResponseDto> recommendPosts(
      Long userId,
      long postId,
      PostRecommendRequestDto postRecommendRequestDto
  ) {
    List<PostTechStack> recommendedPosts = postTechStackRepository.findRecommendPosts(
        postRecommendRequestDto,
        userId,
        postId
    );

    LinkedHashSet<Post> posts = recommendedPosts
        .stream()
        .map(PostTechStack::getPost)
        .filter(p -> p
            .getUser()
            .getId() != userId)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (posts.size() < 4) {
      List<PostTechStack> recommendedPost2 = postTechStackRepository.findRecommendPosts(new PostRecommendRequestDto(
          new ArrayList<>()), userId, postId);
      LinkedHashSet<Post> collect = recommendedPost2
          .stream()
          .map(PostTechStack::getPost)
          .collect(Collectors.toCollection(LinkedHashSet::new));
      for (Post post : collect) {
        posts.add(post);
        if (posts.size() >= 4) {
          break;
        }
      }
    }
    List<PostRecommendResponseDto> list = new ArrayList<>();

    for (Post post : posts) {
      list.add(PostRecommendResponseDto.PostRecommendToDto(post.getId(), post.getTitle()));
      if (list.size() >= 4) {
        break;
      }
    }

    return list;
  }

  @Transactional
  public PostStatus changePostStatus(
      long userId,
      long postId
  ) {
    User user = userService.findById(userId);
    Post post = findOneById(postId);

    if (!user.isWriterOf(post)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }

    return post.changeStatus();
  }

  private List<PostResponseDto> findPostsTags(List<PostTechStack> postTechStacks) {
    //postTechStack에서 post정보만 리스트로 추출
    LinkedHashSet<Post> posts = postTechStacks
        .stream()
        .map(PostTechStack::getPost)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    //각 post 의 태그 정보 조립
    ArrayList<PostResponseDto> postDtos = new ArrayList<>();
    for (Post post : posts) {
      List<String> tags = postTechStacks
          .stream()
          .filter(postTechStack -> postTechStack
              .getPost()
              .getId() == post.getId())
          .map(postTechStack -> postTechStack
              .getTag()
              .getName())
          .collect(Collectors.toList());
      Long commentCount = commentRepository.countByPost(post);
      User user = post.getUser();
      String profile = cloudinaryUtil.findFileURL(user.getPublicId());

      postDtos.add(PostResponseDto.from(
          profile,
          post.getId(),
          post.getTitle(),
          post.getContent(),
          post.getLikeCount(),
          post.getViewCount(),
          post.getStatus(),
          tags,
          commentCount
      ));
    }
    return postDtos;
  }

  private List<String> createTagNames(List<Tag> tags) {
    return tags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());
  }

  private List<PostTechStack> createPostTechStacks(
      List<Tag> tags,
      Post post
  ) {
    return tags
        .stream()
        .map(tag -> PostTechStack.createPostTechStack(tag, post))
        .collect(Collectors.toList());
  }
}
