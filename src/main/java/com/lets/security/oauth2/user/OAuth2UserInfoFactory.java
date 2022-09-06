package com.lets.security.oauth2.user;


import com.lets.security.oauth2.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String accessToken, String registrationId, Map<String, Object> attributes) {
        if(registrationId.equals(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(accessToken, attributes);
        } else if (registrationId.equals(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(accessToken, attributes);
        } else if (registrationId.equals(AuthProvider.kakao.toString())) {
            return new KakaoOAuth2UserInfo(accessToken, attributes);
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }
    }
}
