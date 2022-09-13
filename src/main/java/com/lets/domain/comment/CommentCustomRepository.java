package com.lets.domain.comment;

import java.util.List;

import com.lets.web.dto.comment.CommentSearchRequestDto;

public interface CommentCustomRepository {
  List<Comment> findComments(CommentSearchRequestDto search);
}
