package com.lets.security;


import com.lets.domain.user.User;
import com.lets.security.oauth2.AuthProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserPrincipal implements OAuth2User, UserDetails, Principal {
    private Long id;
    private String nickname;
    private String socialLoginId;
    private AuthProvider authProvider;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String nickname, String socialLoginId, AuthProvider authProvider, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nickname = nickname;
        this.socialLoginId = socialLoginId;
        this.authProvider = authProvider;
        this.authorities = authorities;
    }
    /*
    getAuthenticationFromJWT 사용
     */
    public static UserPrincipal create(Long id, Collection<? extends GrantedAuthority> authorities){
        return new UserPrincipal(id, null, null, null, authorities);
    }

    /*
    테스트 사용
     */
    public static UserPrincipal create(Long id, User user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserPrincipal(
                id,
                user.getNickname(),
                user.getSocialLoginId(),
                user.getAuthProvider(),
                authorities
        );
    }
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserPrincipal(
                user.getId(),
                user.getNickname(),
                user.getSocialLoginId(),
                user.getAuthProvider(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public AuthProvider getAuthProvider(){
        return authProvider;
    }

    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}