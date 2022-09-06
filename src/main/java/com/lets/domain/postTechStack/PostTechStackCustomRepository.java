package com.lets.domain.postTechStack;

import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostTechStackCustomRepository {
        List<PostTechStack> findPostTechStacks(PostSearchRequestDto search, Pageable pageable);
        List<PostTechStack> findRecommendedPosts(PostRecommendRequestDto search, Long userId, Long id);
}

