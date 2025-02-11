package com.scorer.repo.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.scorer.repo.client.impl.RedisCacheClient;
import com.scorer.repo.service.RateLimiterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubRateLimiterServiceImpl implements RateLimiterService {
	private final RedisCacheClient repositoryCacheService;
	
	private static final int MAX_REQUESTS_PER_HOUR = 5000;
    private static final String RATE_LIMIT_KEY = "github-api-request-count:";

	@Override
	public boolean isRateLimited() {
		String currentHourKey = RATE_LIMIT_KEY + getCurrentHour();
		
		String requestCount = repositoryCacheService.getItem(currentHourKey, String.class);
		
		if (requestCount != null && Integer.parseInt(requestCount) >= MAX_REQUESTS_PER_HOUR) {
            return true;
        }	    
		
		repositoryCacheService.increment(currentHourKey, 1);
		repositoryCacheService.expire(currentHourKey, getTimeLeftInCurrentHour(), TimeUnit.SECONDS);
		
		return false;
	}

}
