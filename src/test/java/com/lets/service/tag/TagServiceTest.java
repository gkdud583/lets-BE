package com.lets.service.tag;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.exception.CustomException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @InjectMocks
    TagService tagService;

    @Mock
    TagRepository tagRepository;

    @Test
    void findOne_TAG_NOT_FOUND_예외(){
        //given
        given(tagRepository.findByName(any()))
                .willReturn(Optional.ofNullable(null));
        //when
        Exception exception  = Assertions.assertThrows(CustomException.class, () -> tagService.findOne(any()));

        //then
        assertEquals("존재하지 않는 태그입니다.", exception.getMessage());
    }

    @Test
    void findOne_성공(){
        //given
        Tag tag = Tag.createTag("spring");

        given(tagRepository.findByName(any()))
                .willReturn(Optional.of(tag));
        //when
        Tag findTag = tagService.findOne(any());
        //then
        assertThat(findTag.getName()).isEqualTo("spring");
    }




}
