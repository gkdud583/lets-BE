package com.lets.service.likePost;

import com.lets.domain.likePost.LikePost;
import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.postTechStack.PostTechStack;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.user.User;
import com.lets.web.dto.likepost.LikePostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class LikePostService {
    private final PostTechStackRepository postTechStackRepository;
    private final LikePostRepository likePostRepository;

    public List<LikePost> findAllByUser(User user){
        return likePostRepository.findAllByUser(user);
    }

    public List<LikePostResponseDto> findLikePosts(User user){
        //likePost 조회
        List<LikePost> likePosts = likePostRepository.findAllByUser(user);

        //Post 리스트 생성
        List<Post> posts = likePosts.stream().map(LikePost::getPost).collect(Collectors.toList());

        //각 post 의 태그 정보 구함
        return findLikePostsTags(likePosts, posts);
    }
    public List<LikePostResponseDto> findLikePostsTags(List<LikePost> likePosts, List<Post> posts){

        //관심글의 태그 정보를 구하기위해 postTechStack 조회
        //한번에 모든 글의 태그 정보를 구해와서 애플리케이션에서 조립
        List<PostTechStack> postTechStacks = postTechStackRepository.findAllByPosts(posts);

        //post의 태그정보 조립
        ArrayList<LikePostResponseDto> likePostDtos = new ArrayList<>();
        for(LikePost likePost : likePosts){
            List<Tag> tags = new ArrayList<>();
            for(PostTechStack postTechStack : postTechStacks){
                if(postTechStack.getPost().getId() == likePost.getPost().getId())
                    tags.add(postTechStack.getTag());
            }
            likePostDtos.add(LikePostResponseDto.likePostToDto(likePost.getPost(), likePost.getStatus(), tags));
        }
        return likePostDtos;
    }

}
