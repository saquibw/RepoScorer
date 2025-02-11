package com.scorer.repo.client;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisClient {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
    
    public <T> void saveItem(String key, T item, Duration ttl) {
        redisTemplate.opsForValue().set(key, item, ttl);
    }
    
    public <T> T getItem(String key, Class<T> clazz) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            return objectMapper.convertValue(result, clazz);
        }
        return null;
    }
    
    public void increment(String key, long delta) {
    	redisTemplate.opsForValue().increment(key, delta);
    }
    
    public void expire(String key, long timeout, TimeUnit unit) {
    	redisTemplate.expire(key, timeout, unit);
    }
}
