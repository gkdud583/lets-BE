package com.lets.service.user;

import static com.lets.exception.ErrorCode.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.lets.domain.likePost.LikePostRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.postTechStack.PostTechStackRepository;
import com.lets.domain.tag.Tag;
import com.lets.domain.tag.TagRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.domain.userTechStack.UserTechStack;
import com.lets.domain.userTechStack.UserTechStackRepository;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.security.AuthProvider;
import com.lets.util.CloudinaryUtil;
import com.lets.web.dto.auth.SignupRequestDto;
import com.lets.web.dto.user.SettingRequestDto;
import com.lets.web.dto.user.SettingResponseDto;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final PostTechStackRepository postTechStackRepository;
    private final LikePostRepository likePostRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final CloudinaryUtil cloudinaryUtil;

    public User signup(SignupRequestDto signupRequest, File profile){
        //새로운 프로필 저장
        String publicId = saveProfile(profile);

        //태그 이름으로 태그 조회
        List<Tag> tags = tagRepository.findAllByNameIn(signupRequest.getTags());

        //user 저장
        validateAccount(signupRequest.getSocialLoginId(), signupRequest.getAuthProvider());
        User user = User.createUser(signupRequest.getNickname(), signupRequest.getSocialLoginId(), signupRequest.getAuthProvider(), publicId);
        userRepository.save(user);

        //UserTechStack 저장
        List<UserTechStack> userTechStacks = new ArrayList<>();
        for(Tag tag : tags){
            UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);
            userTechStacks.add(userTechStack);
        }
        userTechStackRepository.saveAll(userTechStacks);

        return user;
    }
    public void signout(User user){
        //프로필 이미지 삭제
        deleteProfile(user);

        //userTechStack 삭제
        userTechStackRepository.deleteAllByUser(user);

        //postTechStack 삭제
        List<Post> posts = postRepository.findAllByUser(user);
        postTechStackRepository.deleteAllByPost(posts);

        //likePost 삭제
        likePostRepository.deleteAllByPost(posts);

        //post 삭제
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        postRepository.deleteAllById(postIds);

        //user 삭제
        userRepository.deleteById(user.getId());
    }
    public User findBySocialLoginIdAndAuthProvider(String socialLoginId, AuthProvider authProvider){
         return userRepository.findBySocialLoginIdAndAuthProvider(socialLoginId, authProvider)
                 .orElseThrow(() -> new CustomException(ErrorCode.SOCIAL_LOGIN_ID_AND_AUTH_PROVIDER_NOT_THE_SAME));
    }

    public boolean existsById(Long id){
        boolean result = userRepository.existsById(id);
        if(!result)
            throw new CustomException(USER_NOT_FOUND);
        return true;
    }
    public void validateNickname(String name){
        if(userRepository.existsByNickname(name)){
            throw new CustomException(ErrorCode.DUPLICATE_NAME);
        }

    }


    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

    }

    public SettingResponseDto getSetting(long userId) {
        User user = findById(userId);
        List<UserTechStack> userTechStacks = userTechStackRepository.findAllByUser(user);
        String profile = cloudinaryUtil.findFileURL(user.getPublicId());

        return SettingResponseDto.from(profile, user.getNickname(), userTechStacks);
    }
    public void change(User user, String profileStatus, File profile, SettingRequestDto settingRequestDto){

        String publicId = user.getPublicId();

        //프로필 유지가 아니라면 이전 프로필 이미지 삭제, 프로필 이미지 저장
        if(!profileStatus.equals("KEEP")) {
            //이전 프로필 이미지 삭제;
            deleteProfile(user);
            //새로운 프로필 이미지 저장
            publicId = saveProfile(profile);


        }

        //현재 유저의 기술 스택을 모두 지움.
        userTechStackRepository.deleteAllByUser(user);

        //settingDto에 있는 태그 이름 정보를 이용해 태그 조회
        List<Tag> findTags = tagRepository.findAllByNameIn(settingRequestDto.getTags());


        //UserTechStack 저장
        List<UserTechStack> userTechStacks = new ArrayList<>();
        for(Tag tag : findTags){
            UserTechStack userTechStack = UserTechStack.createUserTechStack(tag, user);
            userTechStacks.add(userTechStack);
        }
        userTechStackRepository.saveAll(userTechStacks);


        //설정 변경
        /*
         변경 전 이름 -> "user1", 변경 할 이름 -> "user1"로 같다면
         validateNickname() 호출 시 db에 존재하는 데이터로 인해 예외 던짐
         그래서 새로운 이름을 설정 했을경우만 validateNickname() 호출
         */
        if(!settingRequestDto.getNickname().equals(user.getNickname())){
            //새로운 이름으로 변경
            validateNickname(settingRequestDto.getNickname());
        }
        user.change(publicId, settingRequestDto.getNickname());
    }

    /**
     * 새로운 프로필 저장
     */
    private String saveProfile(File profile){
        String publicId = "default";

        //새로 설정한 이미지가 기본 이미지가 아님 -> 새로운 이미지 저장
        if(profile != null)
            publicId = cloudinaryUtil.saveFile(profile);

        return publicId;
    }
    /**
     * 기존 프로필 삭제
     */
    private void deleteProfile(User user){
        //이전에 설정해 놓은 이미지가 기본 이미지가 아님 -> 기존 이미지 삭제
        if(!user.getPublicId().equals("default")){
            cloudinaryUtil.deleteFile(user.getPublicId());
        }

    }

    /**
     * 중복 회원 가입인지 확인
     */
    private void validateAccount(String socialLoginId, AuthProvider authProvider){
        if(userRepository.existsBySocialLoginIdAndAuthProvider(socialLoginId, authProvider)){
            throw new CustomException(ErrorCode.DUPLICATE_ACCOUNT);
        }
    }


}
