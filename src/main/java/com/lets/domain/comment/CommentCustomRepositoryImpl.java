package com.lets.domain.comment;

import com.lets.domain.post.Post;
import com.lets.web.dto.comment.CommentSearchRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.lets.domain.comment.QComment.comment;
import static com.lets.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findComments(CommentSearchRequestDto search) {
        return jpaQueryFactory.selectFrom(comment)
                .where(eqPost(search.getPost()))
                .join(comment.user, user).fetchJoin()
                .orderBy(comment.createdDate.asc())
                .fetch();
    }

    private BooleanExpression eqPost(Post post){
        if(post == null) return null;

        return comment.post.eq(post);
    }
}
