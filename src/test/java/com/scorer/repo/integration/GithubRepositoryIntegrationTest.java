package com.scorer.repo.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.scorer.repo.config.TestSecurityConfig;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class) 
@TestPropertySource(properties = "spring.profiles.active=test") 
public class GithubRepositoryIntegrationTest {
	private static final int REDIS_PORT = 6379;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;
	
	static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("GITHUB_TOKEN", dotenv.get("GITHUB_TOKEN"));
    }
	
	@DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        String githubToken = System.getProperty("GITHUB_TOKEN");
        if (githubToken != null && !githubToken.isEmpty()) {
            registry.add("github.tokens", () -> githubToken);
        } else {
            throw new IllegalStateException("GITHUB_TOKEN environment variable is not set!");
        }
    }


	@BeforeAll
	static void startRedisContainer() {
		try {
			GenericContainer<?> redisContainer =
					new GenericContainer<>(DockerImageName.parse("redis:7.0"))
					.withExposedPorts(REDIS_PORT)
					.waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1));
			redisContainer.start();
			System.setProperty("spring.data.redis.host", redisContainer.getHost());
			System.setProperty("spring.data.redis.port", redisContainer.getFirstMappedPort().toString());
		} catch (Exception e) {
			System.out.println("Error starting Redis container: " + e.getMessage());
		}
	}

	@Test
	void testGetRepositoriesAndCacheInRedis() throws Exception {
		String username = "user";
		String password = "password";


		ResponseEntity<String> response = restTemplate
				.withBasicAuth(username, password)
				.getForEntity(
						"http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1",
						String.class
						);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

		String redisKey = "github_repos_Java_2020-01-01_1";
		Object result = redisTemplate.opsForValue().get(redisKey);

		assertNotNull(result, "Data from GitHub should be stored in Redis");
	}
}
