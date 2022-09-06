package com.lets.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;

public class CookieUtilTest {
    private final CookieUtil cookieUtil = new CookieUtil();
    @Test
    public void createCookie(){
        //given
        //when
        Cookie accessToken = cookieUtil.createCookie("accessToken", "123");

        //then
        Assertions.assertThat(accessToken.getPath()).isEqualTo("/");
    }

}
