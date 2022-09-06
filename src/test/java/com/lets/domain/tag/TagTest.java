package com.lets.domain.tag;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest {
    @Test
    public void createTag(){
        //given
        //when
        Tag tag = Tag.createTag("tag1");

        //then
        assertThat(tag.getName()).isEqualTo("tag1");
    }
}
