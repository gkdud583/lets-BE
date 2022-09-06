package com.lets.web.dto.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettingRequestDto {
    @NotBlank
    private String profile;
    @NotBlank
    private String nickname;
    private List<String> tags = new ArrayList<>();

    public void setTags(List<String> tags){
        this.tags = tags;
    }
}
