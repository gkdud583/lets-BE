package com.lets.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private List<String> tags;


}
