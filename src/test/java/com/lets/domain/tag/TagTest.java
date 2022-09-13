package com.lets.domain.tag;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TagTest {
  @Test
  @DisplayName("createTag메서드는 태그를 생성한다")
  public void createTag() {
    //given
    //when
    Tag tag = Tag.createTag("tag1");

    //then
    assertThat(tag.getName()).isEqualTo("tag1");
  }
}
