package com.scorer.repo.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.scorer.repo.client.impl.RedisCacheClient;
import com.scorer.repo.config.GithubConfig;
import com.scorer.repo.exception.RateLimitExceededException;
import com.scorer.repo.service.RateLimiterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRateLimiterServiceImpl implements RateLimiterService {
	private final RedisCacheClient repositoryCacheService;
	private final GithubConfig githubConfig;
	
    private static final String RATE_LIMIT_KEY = "github_rate_limit";
    
    public String getAvailableToken() throws RateLimitExceededException {
    	List<String> tokenPool = githubConfig.getTokens();
        String currentMinute = getCurrentMinute();

        for (String token : tokenPool) {
            String tokenKey = RATE_LIMIT_KEY + token + ":" + currentMinute;
            String requestCount = repositoryCacheService.getItem(tokenKey, String.class);
            
            log.info("Token {} used {}/{} times this minute", token, requestCount, githubConfig.getMaxRequestLimit());

            int currentCount = (requestCount != null) ? Integer.parseInt(requestCount) : 0;
            if (currentCount < githubConfig.getMaxRequestLimit()) {
            	repositoryCacheService.increment(tokenKey, 1);
                repositoryCacheService.expire(tokenKey, 60, TimeUnit.SECONDS);
                
                return token;
            }
        }
        
        throw new RateLimitExceededException("All tokens have exceeded the rate limit.");
    }
}
