package com.scorer.repo.utils;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import com.scorer.repo.dto.RepositoryDto;

public class RepositoryScoreBuilder {
	private final RepositoryDto repository;
	
    private double score;

    public RepositoryScoreBuilder(RepositoryDto repository) {
        this.repository = repository;
    }

    public RepositoryScoreBuilder withStars() {
        if (repository.getStars() > 0) {
            score += repository.getStars() * 2.0;
        }
        return this;
    }

    public RepositoryScoreBuilder withForks() {
        if (repository.getForks() > 0) {
            score += repository.getForks() * 1.5;
        }
        return this;
    }

    public RepositoryScoreBuilder withWatchers() {
        if (repository.getWatchers() > 0) {
            score += repository.getWatchers() * 1.2;
        }
        return this;
    }

    public RepositoryScoreBuilder withIssuesPenalty() {
        if (repository.getIssues() > 0) {
            score += repository.getIssues() * -0.5;
        }
        return this;
    }

    public RepositoryScoreBuilder withRecency() {
        OffsetDateTime updatedAt = OffsetDateTime.parse(repository.getLastUpdated());
        long daysSinceUpdate = ChronoUnit.DAYS.between(updatedAt, OffsetDateTime.now());

        score += Math.exp(-0.01 * daysSinceUpdate) * 10;
        return this;
    }

    public double build() {
        return score;
    }
}
