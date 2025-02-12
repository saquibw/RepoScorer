package com.scorer.repo.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.scorer.repo.dto.RepositoryDto;
import com.scorer.repo.service.impl.GithubRepositoryScorerServiceImpl;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class RepositoryScorerServiceTest {

    private final RepositoryScorerService scorerService = new GithubRepositoryScorerServiceImpl();

    @Test
    void shouldGiveHigherScoreForMoreStars() {
        RepositoryDto repo = createRepository(100, 50, 30, 5, 1);  // 100 stars
        double score = scorerService.calculateScore(repo);
        assertTrue(score > 200);  // Stars contribute significantly
    }

    @Test
    void shouldGiveHigherScoreForMoreForks() {
        RepositoryDto repo = createRepository(50, 100, 30, 5, 1);  // 100 forks
        double score = scorerService.calculateScore(repo);
        assertTrue(score > 150);  
    }

    @Test
    void shouldGiveHigherScoreForMoreWatchers() {
        RepositoryDto repo = createRepository(50, 50, 100, 5, 1);  // 100 watchers
        double score = scorerService.calculateScore(repo);
        assertTrue(score > 120);  
    }

    @Test
    void shouldPenalizeRepositoriesWithManyIssues() {
        RepositoryDto repoLowIssues = createRepository(50, 50, 50, 1, 1);  // 1 issue
        RepositoryDto repoHighIssues = createRepository(50, 50, 50, 50, 1); // 50 issues

        double scoreLowIssues = scorerService.calculateScore(repoLowIssues);
        double scoreHighIssues = scorerService.calculateScore(repoHighIssues);

        assertTrue(scoreLowIssues > scoreHighIssues);  
    }

    @Test
    void shouldPenalizeOldRepositories() {
        RepositoryDto repoRecent = createRepository(50, 50, 50, 5, 1);   // 1 day old
        RepositoryDto repoOld = createRepository(50, 50, 50, 5, 365);  // 1 year old

        double scoreRecent = scorerService.calculateScore(repoRecent);
        double scoreOld = scorerService.calculateScore(repoOld);

        assertTrue(scoreRecent > scoreOld);  
    }

    @Test
    void shouldHandleZeroValuesProperly() {
        RepositoryDto repo = createRepository(0, 0, 0, 0, 1);
        double score = scorerService.calculateScore(repo);
        assertTrue(score >= 0);
    }

    private RepositoryDto createRepository(int stars, int forks, int watchers, int issues, int daysAgo) {
        OffsetDateTime lastUpdated = OffsetDateTime.now().minusDays(daysAgo);
        return new RepositoryDto(
                "test-repo",
                "test/repo",
                "Test description",
                stars,
                forks,
                watchers,
                issues,
                lastUpdated.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                0.0
        );
    }
}

