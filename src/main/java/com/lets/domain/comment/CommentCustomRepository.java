package com.lets.domain.comment;

import com.lets.web.dto.comment.CommentSearchRequestDto;

import java.util.List;

public interface CommentCustomRepository {
    List<Comment> findComments(CommentSearchRequestDto search);
}
