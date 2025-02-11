package com.scorer.repo.service;

import com.scorer.repo.response.RepositoryResponse;

public interface RepositoryService {
	RepositoryResponse fetchRepositories(String language, String createdAfter, Integer page);
}
