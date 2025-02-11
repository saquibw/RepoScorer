package com.scorer.repo.service;

import com.scorer.repo.dto.RepositoryDto;

public interface RepositoryScorerService {
	 double calculateScore(RepositoryDto repository);
}
