package com.scorer.repo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "github")
@Data
public class GithubConfig {
	private String apiUrl;
    private String cachePrefix;
    private long cacheTtl;
    private long maxRequestLimit;
    private String tokens;
    
    public List<String> getTokens() {
        return Arrays.asList(tokens.split(","));
    }
}
