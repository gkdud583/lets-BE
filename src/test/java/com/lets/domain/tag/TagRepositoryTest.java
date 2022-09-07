package com.lets.domain.tag;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lets.config.QueryDslConfig;

@DataJpaTest
@Import(QueryDslConfig.class)
public class TagRepositoryTest {
  @Autowired
  TagRepository tagRepository;

  @AfterEach
  public void teardown() {
    tagRepository.deleteAllInBatch();
  }

  @DisplayName("findByName메서드는 태그 이름으로 태그를 조회한다")
  @Test
  public void findByName() {
    //given
    Tag tag = Tag.createTag("spring");
    tagRepository.save(tag);

    //when
    Optional<Tag> findTag = tagRepository.findByName("spring");

    //then
    assertThat(findTag
                   .get()
                   .getId()).isEqualTo(tag.getId());
  }

  @DisplayName("findAllByNameIn메서드는 태그 이름으로 모든 태그를 조회한다")
  @Test
  public void findAllByNameIn() {
    //given
    Tag tag1 = Tag.createTag("spring");
    tagRepository.save(tag1);

    Tag tag2 = Tag.createTag("jpa");
    tagRepository.save(tag2);

    //when
    List<Tag> result = tagRepository.findAllByNameIn(Arrays.asList("spring", "jpa"));

    //then
    assertThat(result.size()).isEqualTo(2);
  }
}
