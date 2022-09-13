package com.lets.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
  @Value("${cloud_name}")
  private String cloud_name;

  @Value("${api_key}")
  private String api_key;

  @Value("${api_secret}")
  private String api_secret;

  @Value("${secure}")
  private String secure;

  @Bean
  public Cloudinary cloudinary() {
    Map<String, String> configurationMap = new HashMap<>();
    configurationMap.put("cloud_name", cloud_name);
    configurationMap.put("api_key", api_key);
    configurationMap.put("api_secret", api_secret);
    configurationMap.put("secure", secure);

    return new Cloudinary(configurationMap);
  }
}
