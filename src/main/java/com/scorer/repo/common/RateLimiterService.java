package com.scorer.repo.common;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public interface RateLimiterService {
	boolean isRateLimited();
	
	default String getCurrentHour() {
        return String.valueOf(Instant.now().getEpochSecond() / 3600);
    }
	
	default long getTimeLeftInCurrentHour() {
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime endOfCurrentHour = now.withMinute(59).withSecond(59).withNano(0);
	    return Duration.between(now, endOfCurrentHour).getSeconds();
	}
}
