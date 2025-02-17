package com.scorer.repo.service.impl;

import java.time.Duration;
import org.springframework.stereotype.Service;
import com.scorer.repo.client.CacheClient;
import com.scorer.repo.client.RepositoryClient;
import com.scorer.repo.config.GithubConfig;
import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.RateLimiterService;
import com.scorer.repo.service.RepositoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRepositoryServiceImpl implements RepositoryService{

	private final RepositoryClient githubRepositoryClient;
	private final CacheClient repositoryCacheClient;
	private final RateLimiterService githubRateLimiterService;
	private final GithubConfig githubConfig;

	@Override
	public RepositoryResponse fetchRepositories(String language, String createdAfter, Integer page) {
		String cacheKey = githubConfig.getCachePrefix() + language + ":" + createdAfter + page;
		log.info(cacheKey);

		RepositoryResponse cachedRepositories = repositoryCacheClient.getItem(cacheKey, RepositoryResponse.class);

		if (cachedRepositories != null) {
			log.info("Fetching from Redis Cache!");
			return cachedRepositories;
		}
		
		if (githubRateLimiterService.isRateLimited()) {
	        log.warn("GitHub API rate limit reached! Cannot fetch data at this time.");
	        throw new RuntimeException("GitHub API rate limit exceeded. Please try again later.");
	    }

		String query = "language:" + language + "+created:>" + createdAfter;

		var githubRepositoryResponse = githubRepositoryClient.get(query, page);

		repositoryCacheClient.saveItem(cacheKey, githubRepositoryResponse, Duration.ofMinutes(githubConfig.getCacheTtl()));

		return githubRepositoryResponse;
	}

}
