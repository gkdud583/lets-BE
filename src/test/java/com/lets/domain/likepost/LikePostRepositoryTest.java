package com.lets.domain.likepost;

import com.lets.config.QueryDslConfig;
import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.oauth2.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)

public class LikePostRepositoryTest {
    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User user;
    private Post post;
    private LikePost likePost;

    @BeforeEach
    void setup(){
        user = User.createUser("user1", "123", AuthProvider.google, "default");
        userRepository.save(user);

        post = Post.createPost(user, "title1", "content1");
        postRepository.save(post);

        likePost = LikePost.createLikePost(user, post);
        likePostRepository.save(likePost);

    }

    @DisplayName("유저의 모든 관심글을 조회합니다.")
    @Test
    public void findAllByUser(){
        //given

        //when
        List<LikePost> result = likePostRepository.findAllByUser(user);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(likePost.getId());

    }
    @DisplayName("특정 글의 모든 관심글을 삭제합니다.")
    @Test
    public void deleteAllByPost(){
        //given

        //when
        likePostRepository.deleteAllByPost(Arrays.asList(post));

        //then
        long result = likePostRepository.count();
        assertThat(result).isEqualTo(0);
    }
    @DisplayName("특정 유저와 특정 글의 관심글을 조회합니다.")
    @Test
    public void findByUserIdAndPostId(){
        //given

        //when
        Optional<LikePost> result = likePostRepository.findByUserIdAndPostId(user.getId(), post.getId());

        //then
        assertThat(result.get().getId()).isEqualTo(likePost.getId());
    }

}
