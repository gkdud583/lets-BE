package com.lets.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.security.JwtAuthenticationFilter;
import com.lets.security.JwtTokenProvider;
import com.lets.security.RestAuthenticationEntryPoint;
import com.lets.security.oauth2.CustomOAuth2UserService;
import com.lets.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.lets.service.user.UserService;
import com.lets.util.CookieUtil;
import com.lets.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@EnableWebMvc
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .formLogin()
                    .disable()
                .httpBasic()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .and()
                .authorizeRequests()
                    .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                        .permitAll()
                    .antMatchers(HttpMethod.POST, "/api/auth/signin", "/api/auth/signup", "/api/auth/silent-refresh")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/posts/**")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/tags/**")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/auth/exists/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated()
                        .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler);

        // Add our custom Token based authentication filter
        http.addFilterBefore(new JwtAuthenticationFilter(objectMapper, userService, jwtTokenProvider, cookieUtil, redisUtil), UsernamePasswordAuthenticationFilter.class);

    }

}
