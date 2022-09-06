package com.lets.web.dto.comment;

import com.lets.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentSearchRequestDto {
    private Post post;
}
