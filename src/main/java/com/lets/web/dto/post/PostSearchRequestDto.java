package com.lets.web.dto.post;


import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostSearchRequestDto {
    private String status;
    private List<String> tags;
}
