package com.scorer.repo.common;

public interface RepositoryService {
	RepositoryResponse fetchRepositories(String language, String createdAfter, Integer page);
}
