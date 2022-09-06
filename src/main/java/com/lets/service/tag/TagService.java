package com.lets.service.tag;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagService {
  private final TagRepository tagRepository;

  public List<Tag> findAll() {
    return tagRepository.findAll();
  }
}
