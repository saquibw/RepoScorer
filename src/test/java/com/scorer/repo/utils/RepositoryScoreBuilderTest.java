package com.scorer.repo.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.scorer.repo.dto.RepositoryDto;

public class RepositoryScoreBuilderTest {
	@Test
    void shouldGiveHigherScoreForMoreStars() {
        RepositoryDto repo = createRepository(100, 50, 30, 5, 1);  // 100 stars
        double score = new RepositoryScoreBuilder(repo)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();

        assertTrue(score > 200);  // Stars contribute significantly
    }

    @Test
    void shouldGiveHigherScoreForMoreForks() {
        RepositoryDto repo = createRepository(50, 100, 30, 5, 1);  // 100 forks
        double score = new RepositoryScoreBuilder(repo)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();
        assertTrue(score > 150);
    }

    @Test
    void shouldGiveHigherScoreForMoreWatchers() {
        RepositoryDto repo = createRepository(50, 50, 100, 5, 1);  // 100 watchers
        double score = new RepositoryScoreBuilder(repo)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();
        assertTrue(score > 120);
    }

    @Test
    void shouldPenalizeRepositoriesWithManyIssues() {
        RepositoryDto repoLowIssues = createRepository(50, 50, 50, 1, 1);  // 1 issue
        RepositoryDto repoHighIssues = createRepository(50, 50, 50, 50, 1); // 50 issues

        double scoreLowIssues = new RepositoryScoreBuilder(repoLowIssues)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();

        double scoreHighIssues = new RepositoryScoreBuilder(repoHighIssues)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();

        assertTrue(scoreLowIssues > scoreHighIssues);
    }

    @Test
    void shouldPenalizeOldRepositories() {
        RepositoryDto repoRecent = createRepository(50, 50, 50, 5, 1);   // 1 day old
        RepositoryDto repoOld = createRepository(50, 50, 50, 5, 365);  // 1 year old

        double scoreRecent = new RepositoryScoreBuilder(repoRecent)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();

        double scoreOld = new RepositoryScoreBuilder(repoOld)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();

        assertTrue(scoreRecent > scoreOld);
    }

    @Test
    void shouldHandleZeroValuesProperly() {
        RepositoryDto repo = createRepository(0, 0, 0, 0, 1);
        double score = new RepositoryScoreBuilder(repo)
                .withStars()
                .withForks()
                .withWatchers()
                .withIssuesPenalty()
                .withRecency()
                .build();
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
