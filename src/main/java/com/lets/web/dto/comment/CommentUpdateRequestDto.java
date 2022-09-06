package com.lets.web.dto.comment;

import com.lets.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentUpdateRequestDto {
    private String content;

    public CommentUpdateRequestDto(String content){
        this.content = content;
    }

    public void changeComment(Comment comment){
        comment.change(content);
    }
}
