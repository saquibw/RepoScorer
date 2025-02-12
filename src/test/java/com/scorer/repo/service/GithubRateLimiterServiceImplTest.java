package com.scorer.repo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.scorer.repo.client.impl.RedisCacheClient;
import com.scorer.repo.service.impl.GithubRateLimiterServiceImpl;

public class GithubRateLimiterServiceImplTest {

	@Mock
    private RedisCacheClient repositoryCacheService;

    @InjectMocks
    private GithubRateLimiterServiceImpl rateLimiterService;

    private String currentHourKey;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentHourKey = "github-api-request-count:" + getCurrentHour();
    }
    
    @Test
    void shouldReturnTrueWhenRateLimitExceeded() {
        when(repositoryCacheService.getItem(currentHourKey, String.class)).thenReturn("5000");

        boolean result = rateLimiterService.isRateLimited();

        assertTrue(result);
        verify(repositoryCacheService, never()).increment(anyString(), anyLong());
        verify(repositoryCacheService, never()).expire(anyString(), anyLong(), any());
    }
    
    @Test
    void shouldIncrementCounterWhenRateLimitNotExceeded() {
        when(repositoryCacheService.getItem(currentHourKey, String.class)).thenReturn("100");

        boolean result = rateLimiterService.isRateLimited();

        assertFalse(result);
        verify(repositoryCacheService).increment(eq(currentHourKey), eq(1L));
        verify(repositoryCacheService).expire(eq(currentHourKey), anyLong(), eq(TimeUnit.SECONDS));
    }
    
    @Test
    void shouldInitializeCounterWhenCacheIsEmpty() {
        when(repositoryCacheService.getItem(currentHourKey, String.class)).thenReturn(null);

        boolean result = rateLimiterService.isRateLimited();

        assertFalse(result);
        verify(repositoryCacheService).increment(eq(currentHourKey), eq(1L));
        verify(repositoryCacheService).expire(eq(currentHourKey), anyLong(), eq(TimeUnit.SECONDS));
    }
    
    private String getCurrentHour() {
        return String.valueOf(Instant.now().getEpochSecond() / 3600);
    }
}
