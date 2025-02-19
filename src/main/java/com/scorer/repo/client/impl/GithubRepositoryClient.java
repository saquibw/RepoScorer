package com.scorer.repo.client.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.scorer.repo.client.RepositoryClient;
import com.scorer.repo.dto.GithubRepositoryDto;
import com.scorer.repo.dto.RepositoryDto;
import com.scorer.repo.response.GithubRepositoryResponse;
import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.utils.RepositoryScoreBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRepositoryClient implements RepositoryClient {
	private final WebClient githubWebClient;

	@Override
	public RepositoryResponse get(String query, Integer page, String token) {
		GithubRepositoryResponse response = fetchDataFromGithub(query, page, token);

		List<RepositoryDto> repositories = mapResponseItemsToGeneric(response.getItems());

		return new RepositoryResponse(response.getTotal_count(), repositories);
	}

	private GithubRepositoryResponse fetchDataFromGithub(String query, Integer page, String token) {
		log.debug("Using token: " + token); // Log the token
		return githubWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/search/repositories")
						.queryParam("q", query)
						.queryParam("sort", "stars")
						.queryParam("order", "desc")
						.queryParam("per_page", 100)
						.queryParam("page", page)
						.build())
				.headers(headers -> headers.setBearerAuth(token))
				.retrieve()
				.bodyToMono(GithubRepositoryResponse.class)
				.block();
	}

	private List<RepositoryDto> mapResponseItemsToGeneric(List<GithubRepositoryDto> githubRepositories) {
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
							0.0
							);

					repositoryDto.setScore(new RepositoryScoreBuilder(repositoryDto)
							.withStars()
							.withForks()
							.withWatchers()
							.withIssuesPenalty()
							.withRecency()
							.build());;

							return repositoryDto;
				})
				.toList();
	}

}
