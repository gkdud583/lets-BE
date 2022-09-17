package com.lets.web;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.service.tag.TagService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagControllerTest {
  @LocalServerPort
  private int port;

  @Autowired
  TagService tagService;

  @Autowired
  TagRepository tagRepository;

  @Autowired
  private TestRestTemplate testRestTemplate;

  private Tag tag;

  @BeforeEach
  void before() {
    tag = tagRepository.save(Tag.createTag("spring"));
  }

  @Test
  @DisplayName("findAll메서드는 모든 태그 이름을 반환한다")
  void findAll() {
    //given
    String url = "http://localhost:" + port + "/api/tags";

    //when
    ResponseEntity<List<String>> res = testRestTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        }
    );

    //then
    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res
                   .getBody()
                   .size()).isEqualTo(1);

  }
}
