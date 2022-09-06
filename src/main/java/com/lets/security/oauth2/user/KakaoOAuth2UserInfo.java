package com.lets.security.oauth2.user;


import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{
    public KakaoOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        super(accessToken, attributes);
    }

    @Override
    public String getSocialLoginId() {
        String id = String.valueOf(attributes.get("id"));
        return id;
    }
//
//    @Override
//    public String getEmail() {
//        HashMap map = (HashMap) attributes.get("kakao_account");
//        String email = (String) map.get("email");
//        return email;
//
//    }
}
