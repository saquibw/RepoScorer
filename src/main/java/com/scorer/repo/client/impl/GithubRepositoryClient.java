package com.scorer.repo.client.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.scorer.repo.client.RepositoryClient;
import com.scorer.repo.dto.GithubRepositoryDto;
import com.scorer.repo.dto.RepositoryDto;
import com.scorer.repo.response.GithubRepositoryResponse;
import com.scorer.repo.response.RepositoryResponse;
import com.scorer.repo.service.RepositoryScorerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubRepositoryClient implements RepositoryClient {
	private final WebClient githubWebClient;
	private final RepositoryScorerService repositoryScorerService;

	@Override
	public RepositoryResponse get(String query, Integer page) {
		GithubRepositoryResponse response = fetchDataFromGithub(query, page);

		List<RepositoryDto> repositories = mapResponseItemsToGeneric(response.getItems());

		return new RepositoryResponse(response.getTotal_count(), repositories);
	}
	
	private GithubRepositoryResponse fetchDataFromGithub(String query, Integer page) {
		return githubWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/search/repositories")
						.queryParam("q", query)
						.queryParam("sort", "stars")
						.queryParam("order", "desc")
						.queryParam("per_page", 100)
						.queryParam("page", page)
						.build())
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
							0.0  // Placeholder for the score
							);

					double score = repositoryScorerService.calculateScore(repositoryDto);

					repositoryDto.setScore(score);

					return repositoryDto;
				})
				.toList();
	}

}
