package com.lets.security.oauth2.user;

import com.lets.domain.user.User;
import com.lets.security.oauth2.AuthProvider;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public abstract class OAuth2UserInfo {
    protected String accessToken;
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public User toEntity(String registrationId){

        return User.createUser(UUID.randomUUID().toString(), getSocialLoginId(), AuthProvider.valueOf(registrationId), null);
    }
    public abstract String getSocialLoginId();

}
