# RepoScorer

RepoScorer is a Spring Boot application that fetches and scores GitHub repositories based on certain criteria, such as stars, forks, watchers, and issues. It uses the GitHub API to fetch repositories based on specified parameters (such as programming language and creation date) and calculates a score for each repository. The results are then cached using Redis to improve performance for subsequent requests.

## Features

- Fetch repositories from GitHub using specific queries (language, creation date).
- Calculate a score for each repository based on various metrics (stars, forks, watchers, issues).
- Cache results in Redis to speed up future requests.
- Exposes a REST API with Swagger UI for easy testing and interaction.

## Endpoints

You can access the REST API documentation via Swagger at the following URL:

[Swagger UI](http://localhost:8080/swagger-ui/index.html)

- `GET /api/github/repositories`
    - **Parameters:**
      - `language`: Programming language to filter repositories (e.g., `java`, `python`).
      - `created_after`: Filter repositories created after this date (in `YYYY-MM-DD` format).
      - `page`: Page number for paginated results (default is 1).
      
### Example Request

GET http://localhost:8080/api/github/repositories?language=java&created_after=2022-01-01&page=1

### Example Response

```json
{
  "total_count": 40,
  "repositories": [
    {
      "name": "RepoName",
      "fullName": "RepoOwner/RepoName",
      "description": "A description of the repository.",
      "stars": 1500,
      "forks": 300,
      "watchers": 100,
      "issues": 5,
      "updated_at": "2025-02-10T00:00:00Z",
      "score": 85.5
    }
  ]
}
```

### Setup
To run the application locally, follow the steps below.

Prerequisites
* Docker
* Docker Compose
* Java 21+ (for building from source)
* Gradle (if building from source)

### Running with Docker
To run the application and Redis using Docker, follow these steps:

Clone the repository:

1. git clone https://github.com/your-username/RepoScorer.git
cd RepoScorer

2. Build the Docker images and start the services:
docker-compose up --build

This command will build the Docker images for both the app and the Redis server, then start both containers.

The app will be available at http://localhost:8080.
Redis will run on its default port 6379.
You can access the Swagger UI at http://localhost:8080/swagger-ui/index.html to explore and test the API.



### Running Without Docker
If you prefer to run the application without Docker, follow these steps:

1. Clone the repository:
git clone https://github.com/your-username/RepoScorer.git
cd RepoScorer
2. Build the application using Gradle:
./gradlew build
3. Run the application:
java -jar build/libs/RepoScorer-0.0.1-SNAPSHOT.jar
4. Access the application at http://localhost:8080.


### Redis Setup
The application uses Redis for caching repository data. If you're running without Docker, you need to ensure that Redis is running locally or modify the configuration to connect to a remote Redis instance.

### Testing the API
You can test the API via the Swagger UI at:

http://localhost:8080/swagger-ui/index.html
