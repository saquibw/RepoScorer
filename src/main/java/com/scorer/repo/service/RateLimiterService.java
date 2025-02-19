package com.scorer.repo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.scorer.repo.exception.RateLimitExceededException;

public interface RateLimiterService {
	String getAvailableToken() throws RateLimitExceededException;
	
	default String getCurrentMinute() {
        return String.valueOf(Instant.now().truncatedTo(ChronoUnit.MINUTES).getEpochSecond());
    }
}
