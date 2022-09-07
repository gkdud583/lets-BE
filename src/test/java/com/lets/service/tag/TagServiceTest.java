package com.lets.service.tag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
  @InjectMocks
  TagService tagService;

  @Mock
  TagRepository tagRepository;

  @Test
  @DisplayName("findAll메서드는 모든 태그 이름을 반환한다")
  void findAll() {
    //given
    Tag tag = Tag.createTag("spring");

    given(tagRepository.findAll())
        .willReturn(List.of(tag));

    //when
    List<Tag> result = tagService.findAll();

    //then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result
                   .get(0)
                   .getName()).isEqualTo(tag.getName());
  }
}
