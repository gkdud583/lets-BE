package com.lets.domain.user;

import com.lets.domain.BaseTimeEntity;
import com.lets.security.oauth2.AuthProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "nickname"
        })

})
@Entity
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String publicId;

    @NotBlank
    private String socialLoginId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    private User(String nickname, String socialLoginId, AuthProvider authProvider, String publicId){
        this.nickname = nickname;
        this.socialLoginId = socialLoginId;
        this.authProvider = authProvider;
        this.publicId = publicId;
        this.role = Role.USER;
    }

    //==생성 메서드==//
    public static User createUser(String nickname, String socialLoginId, AuthProvider authProvider, String publicId){
        User user = new User(nickname, socialLoginId, authProvider, publicId);
        return user;
    }



    //==필드값 변경==//
    public void change(String publicId, String nickname){
        this.publicId = publicId;
        this.nickname = nickname;
    }




}
