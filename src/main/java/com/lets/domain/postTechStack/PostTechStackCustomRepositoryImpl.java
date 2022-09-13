package com.lets.domain.postTechStack;

import static com.lets.domain.post.QPost.*;
import static com.lets.domain.postTechStack.QPostTechStack.*;
import static com.lets.domain.tag.QTag.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.lets.domain.post.PostStatus;
import com.lets.web.dto.post.PostRecommendRequestDto;
import com.lets.web.dto.post.PostSearchRequestDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PostTechStackCustomRepositoryImpl implements PostTechStackCustomRepository {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<PostTechStack> findPostTechStacks(
      PostSearchRequestDto search,
      Pageable pageable
  ) {
    return jpaQueryFactory
        .selectFrom(postTechStack)
        .where(
            eqName(search.getTags()),
            eqStatus(search.getStatus())
        )
        .join(postTechStack.post, post)
        .fetchJoin()
        .join(postTechStack.tag, tag)
        .fetchJoin()
        .orderBy(getOrderSpecifier(pageable.getSort()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

  }

  @Override
  public List<PostTechStack> findRecommendedPosts(
      PostRecommendRequestDto search,
      Long userId,
      Long postId
  ) {
    return jpaQueryFactory
        .selectFrom(postTechStack)
        .where(
            eqName(search.getTags()),
            eqStatus("RECRUITING"),
            postNotEq(postId)
        )
        .join(postTechStack.post, post)
        .fetchJoin()
        .join(postTechStack.tag, tag)
        .fetchJoin()
        .orderBy(post.viewCount.desc())
        .fetch();
  }

  private BooleanExpression eqName(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return null;
    }
    return postTechStack.tag.name.in(tags);
  }

  private BooleanExpression eqStatus(String status) {
    if (status == null) {
      return null;
    }
    return post.status.eq(PostStatus.valueOf(status));
  }

  private OrderSpecifier getOrderSpecifier(Sort sort) {
    Path<Object> fieldPath = null;
    for (Sort.Order order : sort) {
      String property = order.getProperty();
      if (property.equals("viewCount")) {
        fieldPath = Expressions.path(Object.class, post, "viewCount");
      } else if (property.equals("createdDate")) {
        fieldPath = Expressions.path(Object.class, post, "createdDate");
      }
    }
    return new OrderSpecifier(Order.DESC, fieldPath);
  }

  private BooleanExpression postNotEq(Long id) {
    return post.id.ne(id);
  }
}
