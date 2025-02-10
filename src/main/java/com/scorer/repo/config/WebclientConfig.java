package com.scorer.repo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebclientConfig {
	private final GithubConfig githubConfig;

    @Bean
    WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl(githubConfig.getApiUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(5 * 1024 * 1024)) // Increase buffer size to 5MB
                        .build())
                .build();
    }

}
