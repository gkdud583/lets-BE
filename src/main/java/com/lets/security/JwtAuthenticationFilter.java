package com.lets.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.domain.user.User;
import com.lets.exception.ErrorCode;
import com.lets.service.user.UserService;
import com.lets.util.CookieUtil;
import com.lets.util.RedisUtil;
import com.lets.web.dto.comment.auth.AuthResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lets.exception.ErrorCode.*;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    public JwtAuthenticationFilter(ObjectMapper objectMapper, UserService userService, JwtTokenProvider jwtTokenProvider, CookieUtil cookieUtil, RedisUtil redisUtil) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieUtil = cookieUtil;
        this.redisUtil = redisUtil;
    }
    /*
    //==flow==//
    1. 사용자가 access token, refresh token 을 가지고 api 요청을 보낸다.
    2. 서버는 access token 이 유효한지 확인한 후에 유효하다면 securityContext 에 저장후 넘긴다.
    3. 만약 access token 이 유효하지 않다면 refreshToken 이 유효한지 확인하고 유효하다면 새로운 refresh token 을 발행해서 넘긴다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        ErrorCode errorCode = ACCESS_TOKEN_NOT_FOUND;
        String accessToken = getAccessTokenFromRequest(request);

        if(StringUtils.hasText(accessToken)) {
            if (jwtTokenProvider.validateToken(accessToken)) {
                Authentication jwtAuthentication = jwtTokenProvider.getAuthenticationFromJWT(accessToken);

                jwtAuthentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            } else {

                errorCode = REFRESH_TOKEN_NOT_FOUND;
                Cookie refreshTokenCookie = cookieUtil.getCookie(request);

                if (refreshTokenCookie != null) {
                    String refreshToken = refreshTokenCookie.getValue();

                    //refresh token 유효한지 확인
                    errorCode = INVALID_REFRESH_TOKEN;
                    if (jwtTokenProvider.validateToken(refreshToken)) {
                        //refresh token 동일한지 확인

//                        errorCode = INVALID_REFRESH_TOKEN;
                        if(redisUtil.getData(refreshToken) != null){
                            Authentication jwtAuthentication = jwtTokenProvider.getAuthenticationFromJWT(refreshToken);

                            //새로운 access token 발행
                            String newAccessToken = jwtTokenProvider.generateAccessToken(jwtAuthentication);

                            //유저 정보 조회
                            User user = userService.findById(jwtTokenProvider.getUserIdFromJWT(refreshToken));

                            //요청 처리하지 않고 바로 응답 보내도록 해야함
                            String res = objectMapper.writeValueAsString(new AuthResponseDto(user.getNickname(), newAccessToken, "ACCESS_TOKEN이 재발행되었습니다. 다시 요청해 주세요."));
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("Application/json;charset=UTF-8");

                            response.getWriter().println(res);
                            return;
                        }
                    }
                }
            }
        }

        request.setAttribute("errorCode", errorCode);
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
