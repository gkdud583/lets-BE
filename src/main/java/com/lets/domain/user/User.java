package com.lets.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import com.lets.domain.BaseTimeEntity;
import com.lets.security.AuthProvider;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "nickname"
    })

})
@Entity
public class User extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  private User(
      String nickname,
      String socialLoginId,
      AuthProvider authProvider,
      String publicId
  ) {
    this.nickname = nickname;
    this.socialLoginId = socialLoginId;
    this.authProvider = authProvider;
    this.publicId = publicId;
    this.role = Role.USER;
  }

  //==생성 메서드==//
  public static User createUser(
      String nickname,
      String socialLoginId,
      AuthProvider authProvider,
      String publicId
  ) {
    User user = new User(nickname, socialLoginId, authProvider, publicId);
    return user;
  }

  public void changePublicId(String publicId) {
    this.publicId = publicId;
  }

  public void changeNickname(String nickname) {
    this.nickname = nickname;
  }
}
