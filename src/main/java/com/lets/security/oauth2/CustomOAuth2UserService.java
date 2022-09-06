package com.lets.security.oauth2;

import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.security.UserPrincipal;
import com.lets.security.oauth2.user.OAuth2UserInfo;
import com.lets.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try{
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        }catch(AuthenticationException ex){
            throw ex;
        }catch(Exception ex){
            //AuthenticationException instance -> will trigger OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){

        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(accessToken, registrationId, oAuth2User.getAttributes());

        Optional<User> userOptional = userRepository.findBySocialLoginIdAndAuthProvider(oAuth2UserInfo.getSocialLoginId(), AuthProvider.valueOf(registrationId));

        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = oAuth2UserInfo.toEntity(registrationId);
        }

        return UserPrincipal.create(user);


    }

}
