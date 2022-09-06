package com.lets.service.tag;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagService {
  private final TagRepository tagRepository;

  public Tag findOne(String name) {
    return tagRepository
        .findByName(name)
        .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
  }

  public List<Tag> findAll() {
    return tagRepository.findAll();
  }

  @Transactional
  public void save(Tag tag) {
    tagRepository.save(tag);
  }
}
