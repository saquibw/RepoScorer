package com.scorer.repo.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class GithubRepositoryIntegrationTest {
	private static final int REDIS_PORT = 6379;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;


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
			log.error("Error starting Redis container", e);
		}
	}

	@Test
	void testGetRepositoriesAndCacheInRedis() throws Exception {
		HttpStatusCode statusCode = restTemplate.getForEntity("http://localhost:" + port + "/api/github/repositories?language=Java&created_after=2020-01-01&page=1", String.class)
				.getStatusCode();
		
		assert(statusCode.is2xxSuccessful());

		String redisKey = "github_repos:Java:2020-01-011";
		Object result = redisTemplate.opsForValue().get(redisKey);

		assertNotNull(result, "Data from GitHub should be stored in Redis");
	}

}
