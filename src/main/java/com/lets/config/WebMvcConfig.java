package com.lets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final long MAX_AGE_SECS = 3600;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")//https://clonehola.herokuapp.com
            .allowedOrigins("http://localhost:3000", "https://lets-frontend.herokuapp.com")
            .allowedHeaders("*")
            .allowedMethods("*")
            .allowCredentials(true)
            .maxAge(MAX_AGE_SECS);
  }
}

