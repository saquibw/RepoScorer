package com.scorer.repo.service.impl;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import com.scorer.repo.client.CacheClient;
import com.scorer.repo.client.RepositoryClient;
import com.scorer.repo.config.GithubConfig;
import com.scorer.repo.exception.RateLimitExceededException;
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
	public RepositoryResponse fetchRepositories(String language, String createdAfter, Integer page) throws RateLimitExceededException {
		String cacheKey = String.format("%s_%s_%s_%s", githubConfig.getCachePrefix(), language, createdAfter, page);

		RepositoryResponse cachedRepositories = repositoryCacheClient.getItem(cacheKey, RepositoryResponse.class);

		if (cachedRepositories != null) {
			log.info("Fetching from Redis Cache!");
			return cachedRepositories;
		}
		
		String token = githubRateLimiterService.getAvailableToken();

		String query = String.format("language:%s+created:>%s", language, createdAfter);

		var githubRepositoryResponse = githubRepositoryClient.get(query, page, token);

		repositoryCacheClient.saveItem(cacheKey, githubRepositoryResponse, Duration.ofMinutes(githubConfig.getCacheTtl()));

		return githubRepositoryResponse;
	}

}
