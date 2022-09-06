package com.lets.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lets.domain.tag.Tag;
import com.lets.service.tag.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {
  private final TagService tagService;

  /**
   * 전체 태그 검색
   */
  @GetMapping
  public List<String> findAll() {
    List<Tag> findTags = tagService.findAll();
    return findTags
        .stream()
        .map(tag -> tag.getName())
        .collect(Collectors.toList());
  }
}
