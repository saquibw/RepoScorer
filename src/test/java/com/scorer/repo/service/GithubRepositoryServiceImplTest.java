package com.scorer.repo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scorer.repo.client.CacheClient;
import com.scorer.repo.client.RepositoryClient;
import com.scorer.repo.config.GithubConfig;
import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.impl.GithubRepositoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GithubRepositoryServiceImplTest {

	@Mock
    private RepositoryClient githubRepositoryClient;
	
	@Mock
    private CacheClient repositoryCacheClient;
	
	@Mock
	private RateLimiterService githubRateLimiterService;
	
	@Mock
    private GithubConfig githubConfig;
	
	@InjectMocks
    private GithubRepositoryServiceImpl githubRepositoryService;
	
	private final String language = "java";
    private final String createdAfter = "2024-01-01";
    private final Integer page = 1;
    private final String cacheKey = "cachePrefix-java:2024-01-011";
    
    private RepositoryResponse mockResponse;
    
    @BeforeEach
    void setUp() {
        mockResponse = new RepositoryResponse(100, null);

        when(githubConfig.getCachePrefix()).thenReturn("cachePrefix-");
    }
    
    @Test
    void shouldFetchFromCacheIfCached() {
        RepositoryResponse cachedResponse = new RepositoryResponse(100, new ArrayList<>());
        when(repositoryCacheClient.getItem(cacheKey, RepositoryResponse.class)).thenReturn(cachedResponse);

        RepositoryResponse response = githubRepositoryService.fetchRepositories(language, createdAfter, page);

        assertNotNull(response);
        assertEquals(100, response.getTotalCount());
        verify(repositoryCacheClient, times(1)).getItem(cacheKey, RepositoryResponse.class);
        verify(githubRepositoryClient, times(0)).get(anyString(), anyInt());
    }
    
    @Test
    void shouldFetchFromGithubIfNotCachedAndRateLimitNotExceeded() {
        // Setup mocks
        String cacheKey = "cachePrefix-java:2024-01-01";
        RepositoryResponse mockResponse = new RepositoryResponse(100, new ArrayList<>());
        when(repositoryCacheClient.getItem(cacheKey, RepositoryResponse.class)).thenReturn(null);
        when(githubRateLimiterService.isRateLimited()).thenReturn(false);
        when(githubRepositoryClient.get("language:java+created:>2024-01-01", 1)).thenReturn(mockResponse);

        RepositoryResponse response = githubRepositoryService.fetchRepositories(language, createdAfter, page);

        assertNotNull(response);
        assertEquals(100, response.getTotalCount());
        verify(repositoryCacheClient, times(1)).saveItem(eq(cacheKey), eq(mockResponse), any());
        verify(githubRepositoryClient, times(1)).get("language:java+created:>2024-01-01", 1);
        verify(githubRateLimiterService, times(1)).isRateLimited();  // Verify rate limiter check
    }
    
    @Test
    void shouldThrowExceptionIfRateLimitExceeded() {
        when(githubRateLimiterService.isRateLimited()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            githubRepositoryService.fetchRepositories(language, createdAfter, page);
        });

        verify(githubRateLimiterService, times(1)).isRateLimited();
        verify(githubRepositoryClient, times(0)).get(anyString(), anyInt());
    }
}
