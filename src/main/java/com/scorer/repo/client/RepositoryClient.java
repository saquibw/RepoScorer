package com.scorer.repo.client;

import com.scorer.repo.response.RepositoryResponse;

public interface RepositoryClient {
	RepositoryResponse get(String query, Integer page, String token);
}
