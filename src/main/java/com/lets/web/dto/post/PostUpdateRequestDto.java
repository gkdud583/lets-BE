package com.lets.web.dto.post;

import com.lets.domain.comment.Comment;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostStatus;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequestDto {
    private String title;
    private String content;
    private List<String> tags;
    private PostStatus status;

    public PostUpdateRequestDto(String title, String content, List<String> tags, PostStatus status) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.status = status;
    }

    public void changePost(Post post){
        post.change(this.title, this.content);
    }
}
