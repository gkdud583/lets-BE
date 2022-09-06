package com.lets.web;


import com.lets.domain.tag.Tag;

import com.lets.domain.tag.TagRepository;
import com.lets.security.oauth2.AuthProvider;
import com.lets.service.tag.TagService;

import com.lets.web.dto.auth.SignupRequestDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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

    private SignupRequestDto signupRequest;
    private Tag tag;

    @BeforeEach
    void before(){
        signupRequest = new SignupRequestDto(null, "user1", "1234", AuthProvider.google, new ArrayList<>());
        tag = Tag.createTag("spring");
        tagService.save(tag);

    }
    @AfterEach
    void tearDown(){
        tagRepository.deleteAllInBatch();
    }

    @Test
    void 태그_조회_성공(){
        //given
        String url = "http://localhost:" + port + "/api/tags";

        //when
        ResponseEntity<List<String>> res = testRestTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});

        //then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().size()).isEqualTo(1);

    }





}
