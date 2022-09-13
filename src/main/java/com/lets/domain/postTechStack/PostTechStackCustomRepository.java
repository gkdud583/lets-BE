package com.lets.domain.postTechStack;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;

public interface PostTechStackCustomRepository {
  List<PostTechStack> findPostTechStacks(
      PostSearchRequestDto search,
      Pageable pageable
  );

  List<PostTechStack> findRecommendedPosts(
      PostRecommendRequestDto search,
      Long userId,
      Long id
  );
}
