package com.lets.domain.userTechStack;

import com.lets.config.QueryDslConfig;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
public class UserTechStackRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    TagRepository tagRepository;


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTechStackRepository userTechStackRepository;

    @AfterEach
    void teardown(){
        userTechStackRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        tagRepository.deleteAllInBatch();
    }
    @DisplayName("유저로 모든 유저 기술 스택을 삭제합니다.")
    @Test
    public void deleteAllByUser() {
        //given
        Tag tag = Tag.createTag("spring");
        tagRepository.save(tag);

        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);
        userTechStackRepository.save(userTechStack);

        em.clear();
        //when
        userTechStackRepository.deleteAllByUser(user);


        //then
        Optional<UserTechStack> result = userTechStackRepository.findById(userTechStack.getId());
        assertThat(result).isEmpty();
    }

    @DisplayName("유저로 모든 유저 기술 스택을 조회합니다.")
    @Test
    public void findAllByUser() {
        //given
        Tag tag = Tag.createTag("spring");
        tagRepository.save(tag);

        User user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);
        userTechStackRepository.save(userTechStack);

        //when
        List<UserTechStack> result = userTechStackRepository.findAllByUser(user);

        //then
        assertThat(result.size()).isEqualTo(1);
    }
}
