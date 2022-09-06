package com.lets.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostRecommendRequestDto {
    private List<String> tags;
}
