package com.lets.service.userTechStack;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lets.domain.userTechStack.UserTechStackRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserTechStackService {
  private final UserTechStackRepository userTechStackRepository;
}
