package com.scorer.repo.client;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface CacheClient {

	<T> void saveItem(String key, T item, Duration ttl);

    <T> T getItem(String key, Class<T> clazz);

    void increment(String key, long delta);

    void expire(String key, long timeout, TimeUnit unit);
}
