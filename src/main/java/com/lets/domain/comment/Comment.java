package com.lets.domain.comment;

import com.lets.domain.BaseTimeEntity;
import com.lets.domain.post.Post;
import com.lets.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Lob
    @NotNull
    @NotEmpty
    private String content;

    private Comment(User user, Post post, String content){
        this.user = user;
        this.post = post;
        this.content = content;
    }

    public static Comment createComment(User user, Post post, String content){
        Comment comment = new Comment(user, post, content);

        post.addComment(comment);

        return comment;
    }
    //==필드값 변경==//
    public Comment change(String content){
        this.content = content;

        return this;
    }

}
