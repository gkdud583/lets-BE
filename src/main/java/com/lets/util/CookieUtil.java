package com.lets.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
  @Value("${app.refreshTokenExpirationInMs}")
  private int refreshTokenExpirationInMs;

  public Cookie createCookie(
      String cookieName,
      String value
  ) {
    Cookie token = new Cookie(cookieName, value);
    token.setHttpOnly(true);
    token.setMaxAge(refreshTokenExpirationInMs);
    token.setPath("/");
    return token;
  }

  public Cookie getCookie(HttpServletRequest req) {
    final Cookie[] cookies = req.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie
          .getName()
          .equals("refreshToken")) {
        return cookie;
      }
    }
    return null;

  }

}
