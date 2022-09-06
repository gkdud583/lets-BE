package com.lets.web.dto.likepost;

import com.lets.domain.likePost.LikePostStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ChangeLikePostStatusResponseDto {
    private Long likeCount;
    private LikePostStatus likePostStatus;

    public ChangeLikePostStatusResponseDto(Long likeCount, LikePostStatus likePostStatus){
        this.likeCount = likeCount;
        this.likePostStatus = likePostStatus;
    }
}
