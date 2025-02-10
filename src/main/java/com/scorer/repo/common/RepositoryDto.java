package com.scorer.repo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDto {
	private String name;
	private String fullName;
    private String description;
    private Integer stars;
    private Integer forks;
    private Integer watchers;
    private Integer issues;
    private String lastUpdated;
    private Double score;
}
