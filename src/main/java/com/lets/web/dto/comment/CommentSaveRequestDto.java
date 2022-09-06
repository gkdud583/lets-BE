package com.lets.web.dto.comment;

import com.lets.domain.comment.Comment;
import com.lets.domain.user.User;
import com.lets.domain.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSaveRequestDto {
    private String content;

    public CommentSaveRequestDto(String content) {
        this.content = content;
    }
}
