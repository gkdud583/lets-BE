package com.lets.util;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class RedisUtil {
    private final RedisTemplate redisTemplate;

    public String getData(String key){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        return value;
    }
    public void setData(String key, String value){

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);

    }

    public void deleteData(String key){
        redisTemplate.delete(key);

    }
}
