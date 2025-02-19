package com.scorer.repo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.scorer.repo.client.impl.RedisCacheClient;
import com.scorer.repo.config.GithubConfig;
import com.scorer.repo.exception.RateLimitExceededException;
import com.scorer.repo.service.impl.GithubRateLimiterServiceImpl;

public class GithubRateLimiterServiceImplTest {

	@Mock
    private RedisCacheClient repositoryCacheService;
	
	@Mock
    private GithubConfig githubConfig;

	@Spy
    @InjectMocks
    private GithubRateLimiterServiceImpl rateLimiterService;

    private static final String TOKEN_1 = "token_123";
    private static final String TOKEN_2 = "token_456";
    private static final String CURRENT_MINUTE = "202402191530";
    private static final long MAX_REQUESTS = 5;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(githubConfig.getTokens()).thenReturn(List.of(TOKEN_1, TOKEN_2));
        when(githubConfig.getMaxRequestLimit()).thenReturn(MAX_REQUESTS);
        
        doReturn(CURRENT_MINUTE).when(rateLimiterService).getCurrentMinute();
    }
    
    @Test
    void shouldReturnTokenWhenUnderLimit() throws RateLimitExceededException {
        String tokenKey1 = String.format("github_rate_limit_%s_%s", TOKEN_1, CURRENT_MINUTE);
        
        when(repositoryCacheService.getItem(eq(tokenKey1), eq(String.class))).thenReturn("2");

        String availableToken = rateLimiterService.getAvailableToken();

        assertEquals(TOKEN_1, availableToken);
        verify(repositoryCacheService).increment(eq(tokenKey1), eq(1L));
        verify(repositoryCacheService).expire(eq(tokenKey1), eq(60L), eq(TimeUnit.SECONDS));
    }
    
    @Test
    void shouldReturnNextTokenIfFirstIsRateLimited() throws RateLimitExceededException {
    	String tokenKey1 = String.format("github_rate_limit_%s_%s", TOKEN_1, CURRENT_MINUTE);
    	String tokenKey2 = String.format("github_rate_limit_%s_%s", TOKEN_2, CURRENT_MINUTE);

        when(repositoryCacheService.getItem(eq(tokenKey1), eq(String.class))).thenReturn(String.valueOf(MAX_REQUESTS));

        when(repositoryCacheService.getItem(eq(tokenKey2), eq(String.class))).thenReturn("3");

        String availableToken = rateLimiterService.getAvailableToken();

        assertEquals(TOKEN_2, availableToken);
        verify(repositoryCacheService).increment(eq(tokenKey2), eq(1L));
        verify(repositoryCacheService).expire(eq(tokenKey2), eq(60L), eq(TimeUnit.SECONDS));
    }
    
    @Test
    void shouldThrowExceptionWhenAllTokensAreRateLimited() {
    	String tokenKey1 = String.format("github_rate_limit_%s_%s", TOKEN_1, CURRENT_MINUTE);
    	String tokenKey2 = String.format("github_rate_limit_%s_%s", TOKEN_2, CURRENT_MINUTE);

        // Both tokens exceeded the rate limit
        when(repositoryCacheService.getItem(eq(tokenKey1), eq(String.class))).thenReturn(String.valueOf(MAX_REQUESTS));
        when(repositoryCacheService.getItem(eq(tokenKey2), eq(String.class))).thenReturn(String.valueOf(MAX_REQUESTS));

        assertThrows(RateLimitExceededException.class, () -> rateLimiterService.getAvailableToken());
    }
    
    @Test
    void shouldReturnTokenWhenCacheIsEmpty() throws RateLimitExceededException {
    	String tokenKey1 = String.format("github_rate_limit_%s_%s", TOKEN_1, CURRENT_MINUTE);

        when(repositoryCacheService.getItem(eq(tokenKey1), eq(String.class))).thenReturn(null);

        String availableToken = rateLimiterService.getAvailableToken();

        assertEquals(TOKEN_1, availableToken);
        verify(repositoryCacheService).increment(eq(tokenKey1), eq(1L));
        verify(repositoryCacheService).expire(eq(tokenKey1), eq(60L), eq(TimeUnit.SECONDS));
    }
}
