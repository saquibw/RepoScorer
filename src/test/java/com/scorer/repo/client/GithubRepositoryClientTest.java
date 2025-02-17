package com.scorer.repo.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import com.scorer.repo.client.impl.GithubRepositoryClient;
import com.scorer.repo.dto.GithubRepositoryDto;
import com.scorer.repo.response.GithubRepositoryResponse;
import com.scorer.repo.response.RepositoryResponse;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class GithubRepositoryClientTest {

	@Mock
    private WebClient.Builder webClientBuilder;
	
	@Mock
    private WebClient webClient;
	
	@Mock
    private WebClient.RequestHeadersUriSpec<?> uriSpec;
	
	@Mock
	private WebClient.RequestHeadersSpec<?> headersSpec;
	
	@Mock
    private WebClient.ResponseSpec responseSpec;
	
	private GithubRepositoryClient githubRepositoryClient;
	
	@BeforeEach
    void setUp() {
		MockitoAnnotations.openMocks(this);
        githubRepositoryClient = new GithubRepositoryClient(webClient);
    }
	
	@Test
	void shouldHandleEmptyResponse() {
	    GithubRepositoryResponse githubResponse = new GithubRepositoryResponse(0, List.of());

	    // Mock WebClient behavior
	    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec); // Fix: Match expected type
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(GithubRepositoryResponse.class)).thenReturn(Mono.just(githubResponse));

	    RepositoryResponse response = githubRepositoryClient.get("language:java", 1);

	    assertNotNull(response);
	    assertEquals(0, response.getTotalCount());
	    assertTrue(response.getRepositories().isEmpty());
	}
	
	@Test
	void shouldFetchRepositoriesSuccessfully() {
	    GithubRepositoryDto repoDto = new GithubRepositoryDto("repo-name", "full-repo-name", 
	            "A sample repo", 100, 50, 20, 5, "2024-01-10T10:00:00Z");
	    GithubRepositoryResponse githubResponse = new GithubRepositoryResponse(1, List.of(repoDto));

	    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
	    when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
	    when(headersSpec.retrieve()).thenReturn(responseSpec);
	    when(responseSpec.bodyToMono(GithubRepositoryResponse.class)).thenReturn(Mono.just(githubResponse));

	    RepositoryResponse response = githubRepositoryClient.get("language:java", 1);

	    assertNotNull(response);
	    assertEquals(1, response.getTotalCount());
	    assertEquals(296.68, response.getRepositories().getFirst().getScore(), 0.01);
	}


}
