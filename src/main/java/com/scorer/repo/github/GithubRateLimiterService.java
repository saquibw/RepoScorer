package com.scorer.repo.github;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.scorer.repo.common.RateLimiterService;
import com.scorer.repo.common.RepositoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubRateLimiterService implements RateLimiterService {
	private final RepositoryCacheService repositoryCacheService;
	
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
