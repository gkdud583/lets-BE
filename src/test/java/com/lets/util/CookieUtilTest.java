package com.lets.util;

import javax.servlet.http.Cookie;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CookieUtilTest {
  private final CookieUtil cookieUtil = new CookieUtil();

  @Test
  @DisplayName("createCookie메서드는 쿠키를 생성한다")
  public void createCookie() {
    //given
    //when
    Cookie accessToken = cookieUtil.createCookie("accessToken", "123");

    //then
    Assertions
        .assertThat(accessToken.getPath())
        .isEqualTo("/");
  }

}
