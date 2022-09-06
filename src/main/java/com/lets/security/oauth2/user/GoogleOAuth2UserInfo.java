package com.lets.security.oauth2.user;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        super(accessToken, attributes);
    }

    @Override
    public String getSocialLoginId() {
        String id = String.valueOf(attributes.get("sub"));
        return id;
    }

//    @Override
//    public String getEmail() {
//        return (String) attributes.get("email");
//    }
}
