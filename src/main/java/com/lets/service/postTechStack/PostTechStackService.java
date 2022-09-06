package com.lets.service.postTechStack;


import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@RequiredArgsConstructor
@Service
public class PostTechStackService {
    private final PostTechStackRepository postTechStackRepository;
    public void save(PostTechStack postTechStack){
        postTechStackRepository.save(postTechStack);
    }
}
