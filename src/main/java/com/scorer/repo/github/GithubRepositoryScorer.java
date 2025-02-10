package com.scorer.repo.github;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.scorer.repo.common.RepositoryDto;
import com.scorer.repo.common.RepositoryScorer;

@Service
public class GithubRepositoryScorer implements RepositoryScorer{
	@Override
    public double calculateScore(RepositoryDto repository) {
        // Stars weight: 2x
        double starsScore = repository.getStars() * 2;

        // Forks weight: 1.5x
        double forksScore = repository.getForks() * 1.5;

        OffsetDateTime updatedAt = OffsetDateTime.parse(repository.getLastUpdated());  // Parse using OffsetDateTime
        long daysSinceUpdate = ChronoUnit.DAYS.between(updatedAt, OffsetDateTime.now()); // Compare with current time
        double recencyScore = Math.max(0, 10 - daysSinceUpdate);  // Penalize old repos


        // Final score: weighted sum of factors
        return starsScore + forksScore + recencyScore;
    }
}
