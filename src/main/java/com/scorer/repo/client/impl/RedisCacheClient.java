package com.scorer.repo.client.impl;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorer.repo.client.CacheClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCacheClient implements CacheClient {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
    
	@Override
    public <T> void saveItem(String key, T item, Duration ttl) {
        redisTemplate.opsForValue().set(key, item, ttl);
    }
    
	@Override
    public <T> T getItem(String key, Class<T> clazz) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            return objectMapper.convertValue(result, clazz);
        }
        return null;
    }
    
	@Override
    public void increment(String key, long delta) {
    	redisTemplate.opsForValue().increment(key, delta);
    }
    
	@Override
    public void expire(String key, long timeout, TimeUnit unit) {
    	redisTemplate.expire(key, timeout, unit);
    }
}
