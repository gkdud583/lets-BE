package com.lets.web.dto.comment;

import com.lets.domain.comment.Comment;
import com.lets.domain.user.User;
import com.lets.util.CloudinaryUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private String profile;
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdTime;

    public CommentResponseDto(Comment comment, String profile){
        this.profile = profile;
        this.id = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdTime = comment.getCreatedDate();
    }

    public static CommentResponseDto CommentToDto(Comment comment, String profile){
        return new CommentResponseDto(comment, profile);
    }

}
