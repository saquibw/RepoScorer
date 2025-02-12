package com.scorer.repo.service.impl;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.scorer.repo.dto.RepositoryDto;
import com.scorer.repo.service.RepositoryScorerService;

@Service
public class GithubRepositoryScorerServiceImpl implements RepositoryScorerService{
	@Override
	public double calculateScore(RepositoryDto repository) {
	    // Assigning weights based on significance
	    double starsScore = repository.getStars() * 2.0;    // Stars have high weight
	    double forksScore = repository.getForks() * 1.5;    // Forks indicate adoption
	    double watchersScore = repository.getWatchers() * 1.2;  // Watchers show interest
	    double issuesPenalty = repository.getIssues() * -0.5;  // Too many issues may indicate maintenance problems

	    // Recency Score: Penalize old repositories
	    OffsetDateTime updatedAt = OffsetDateTime.parse(repository.getLastUpdated());
	    long daysSinceUpdate = ChronoUnit.DAYS.between(updatedAt, OffsetDateTime.now());
	    
	    // Exponential decay function for recency: Newer updates get higher scores
	    double recencyScore = Math.exp(-0.01 * daysSinceUpdate) * 10;

	    // Final Score: Weighted sum of all factors
	    return starsScore + forksScore + watchersScore + recencyScore + issuesPenalty;
	}

}
