package com.scorer.repo.github;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.scorer.repo.common.RepositoryCacheService;
import com.scorer.repo.common.RepositoryDto;
import com.scorer.repo.common.RepositoryResponse;
import com.scorer.repo.common.RepositoryScorer;
import com.scorer.repo.common.RepositoryService;
import com.scorer.repo.config.GithubConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRepositoryService implements RepositoryService{

	private final WebClient githubWebClient;
	private final RepositoryScorer repositoryScorer;
	private final RepositoryCacheService repositoryCacheService;
	private final GithubConfig githubConfig;

	@Override
	public RepositoryResponse fetchRepositories(String language, String createdAfter, Integer page) {
		System.out.println(page);
		String cacheKey = githubConfig.getCachePrefix() + language + ":" + createdAfter + page;

		RepositoryResponse cachedRepositories = repositoryCacheService.getItem(cacheKey, RepositoryResponse.class);

		if (cachedRepositories != null) {
			log.info("Fetching from Redis Cache!");
			return cachedRepositories;
		}

		String query = "language:" + language + "+created:>" + createdAfter;

		GitHubSearchResponse response = fetchDataFromGithub(query, page);

		List<RepositoryDto> repositories = mapResponseItemstoGeneric(response.items);

		var githubRepositoryResponse = new RepositoryResponse(response.total_count, repositories);

		repositoryCacheService.saveItem(cacheKey, githubRepositoryResponse, Duration.ofMinutes(githubConfig.getCacheTtl()));

		return githubRepositoryResponse;
	}

	private GitHubSearchResponse fetchDataFromGithub(String query, Integer page) {
		return githubWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/search/repositories")
						.queryParam("q", query)
						.queryParam("sort", "stars")
						.queryParam("order", "desc")
						.queryParam("per_page", 100)
						.queryParam("page", page)
						.build())
				.retrieve()
				.bodyToMono(GitHubSearchResponse.class)
				.block();
	}

	private List<RepositoryDto> mapResponseItemstoGeneric(List<GithubRepositoryDto> githubRepositories) {
		return githubRepositories.stream()
				.map(repo -> {
					RepositoryDto repositoryDto = new RepositoryDto(
							repo.getName(),
							repo.getFull_name(),
							repo.getDescription(),
							repo.getStargazers_count(),
							repo.getForks_count(),
							repo.getWatchers_count(),
							repo.getOpen_issues_count(),
							repo.getUpdated_at(),
							0.0  // Placeholder for the score
							);

					double score = repositoryScorer.calculateScore(repositoryDto);

					repositoryDto.setScore(score);

					return repositoryDto;
				})
				.toList();
	}


	private record GitHubSearchResponse(int total_count, List<GithubRepositoryDto> items) {}
}
