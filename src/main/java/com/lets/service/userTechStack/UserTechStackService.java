package com.lets.service.userTechStack;

import com.lets.domain.user.User;
import com.lets.domain.userTechStack.UserTechStack;
import com.lets.domain.userTechStack.UserTechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class UserTechStackService {
    private final UserTechStackRepository userTechStackRepository;

    public List<UserTechStack> findAllByUser(User user){
        return userTechStackRepository.findAllByUser(user);
    }
}
