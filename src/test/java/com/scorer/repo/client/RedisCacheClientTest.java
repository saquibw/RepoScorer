package com.scorer.repo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scorer.repo.client.impl.RedisCacheClient;

public class RedisCacheClientTest {
	@Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisCacheClient redisCacheClient;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisCacheClient = new RedisCacheClient(redisTemplate, new ObjectMapper());
    }
    
    @Test
    void shouldSaveItemInCache() {
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(10);

        redisCacheClient.saveItem(key, value, ttl);

        verify(valueOperations).set(key, value, ttl);
    }
    
    @Test
    void shouldRetrieveItemFromCache() {
        String key = "testKey";
        String value = "testValue";

        when(valueOperations.get(key)).thenReturn(value);

        String result = redisCacheClient.getItem(key, String.class);

        assertEquals(value, result);
        verify(valueOperations).get(key);
    }
    
    @Test
    void shouldReturnNullIfKeyNotFound() {
        String key = "missingKey";

        when(valueOperations.get(key)).thenReturn(null);

        String result = redisCacheClient.getItem(key, String.class);

        assertNull(result);
        verify(valueOperations).get(key);
    }
    
    @Test
    void shouldIncrementValueInCache() {
        String key = "counter";

        redisCacheClient.increment(key, 5);

        verify(valueOperations).increment(key, 5);
    }
    
    @Test
    void shouldSetExpirationTimeForKey() {
        String key = "testKey";
        long timeout = 60;

        redisCacheClient.expire(key, timeout, TimeUnit.SECONDS);

        verify(redisTemplate).expire(key, timeout, TimeUnit.SECONDS);
    }
}
