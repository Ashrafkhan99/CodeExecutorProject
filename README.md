CodeRank Executor
Secure code execution service that runs user code in isolated containers for fast feedback.







📚 Table of Contents
Project Overview

Key Features

Quick Start

How to Use

Technical Details

Advanced Usage

Testing This Project

Contributing

Support & Community

Project Details & Roadmap

FAQ

Troubleshooting

Maintenance & Staying Informed

Success Metrics & Default Limits

🌟 Project Overview
What it does
CodeRank Executor is a Spring Boot web service that safely runs submitted code snippets inside short-lived Docker containers. It authenticates users with JSON Web Tokens (JWTs — signed login tokens), stores execution history in PostgreSQL, and exposes a clean REST API (web interface that uses standard HTTP verbs) for integration with web apps, coding lessons, or developer tools.

Problem it solves
Teams often need a trusted backend to execute arbitrary code without exposing their infrastructure. CodeRank Executor enforces resource limits, isolates workloads, and tracks every run, removing the risk of running untrusted code directly on production systems.

Key benefits

🚀 Fast feedback: Get execution results in seconds, ideal for learning platforms or coding challenges.

🔒 Safety first: Docker sandboxes with CPU, memory, and time quotas keep hosts secure.

📊 Traceable: Every submission is stored for analytics, audits, or customer support.

🧰 Integrations ready: REST API and OpenAPI docs make it easy to plug into web portals, CLIs, or IDE extensions.

📈 Operational insight: Built-in metrics (via Micrometer, a metrics-gathering library) and Actuator endpoints for monitoring.

Who it’s for

Non-technical stakeholders: Product owners, educators, and customer success teams who need reliable, secure code execution.

Technical users: Backend developers or DevOps engineers embedding execution capabilities into apps.

Contributors: Developers extending language support or improving infrastructure.

✨ Key Features
JWT-secured authentication – Login and session management using time-boxed JSON Web Tokens, so only verified users can submit code.

Language catalogue – Discover enabled languages (e.g., Python, Java, C++) with human-friendly names and versions to guide users.

Safe execution pipeline – Submissions run inside non-root Docker containers with CPU, memory, and timeout limits, protecting host machines.

Fair usage controls – Per-user concurrency caps and minute-based rate limiting prevent system overload and guarantee fairness.

Execution history – Each run stores source, input, output, status, and timing for later review or analytics.

Observability out of the box – Spring Boot Actuator, structured logs, and Prometheus metrics ensure production readiness.

Admin tools – Role-based endpoints let administrators toggle languages and review recent submissions without touching the database.

OpenAPI documentation – Interactive Swagger UI so developers can explore and test endpoints directly in the browser.

🚀 Quick Start
What You'll Need
Java 17 SDK – Required to compile and run the Spring Boot application.

Maven 3.9+ or the bundled mvnw wrapper – Handles dependency management and builds.

Docker Engine – Powers the sandboxed language containers used for code execution.

Docker Compose (v2) – Simplifies running the API and PostgreSQL together for local development.

PostgreSQL 16 – Stores users, language definitions, and execution history.

💡 Non-technical summary: Install Java, Docker, and Docker Compose. These tools let the service run code safely and remember the results.

Installation (Step-by-Step)
Clone the repository

git clone https://github.com/your-org/coderank-executor.git
cd coderank-executor
What this does: Downloads the project files to your computer.

(Optional) Build the language sandbox images

./languages/build-all.sh
What this does: Prepares the Python, Java, and C++ Docker images used for execution. Skip if you will pull pre-built images from a registry.

Start everything with Docker Compose

docker compose up --build
What this does: Launches PostgreSQL and the API together. The API listens on http://localhost:8080 and connects to the local Docker engine for sandboxing.

Visit the health check

curl http://localhost:8080/api/health
What this does: Confirms the service is running. A healthy response looks like {"status":"UP"}.

📖 How to Use
🧭 Typical user journey: Create an account → log in → view available languages → run code → review output.

1. Register (create a user account)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
        "email": "learner@example.com",
        "password": "StrongP@ssw0rd!"
      }'
Result: Receives a JWT token and expiry (in seconds). Save the token for the next calls.

2. Log in (retrieve a fresh JWT)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
        "email": "learner@example.com",
        "password": "StrongP@ssw0rd!"
      }'
Result: Returns {"token": "...","expiresInSeconds": 1800}.

3. List enabled languages
curl http://localhost:8080/api/languages
Result: Array of languages, e.g., [{ "code": "python", "name": "Python 3.11", "version": "3.11" }, ...].

4. Execute code
TOKEN="paste-your-jwt-here"

curl -X POST http://localhost:8080/api/execute \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "language": "python",
        "source": "print(\"Hello CodeRank\")",
        "stdin": ""
      }'
Result (business meaning): Provides stdout, stderr, status, and execTimeMs. Status SUCCESS means the learner’s solution ran correctly; COMPILE_ERROR, RUNTIME_ERROR, or TIMEOUT highlight issues to fix.

5. Explore the API interactively
Visit http://localhost:8080/swagger-ui/index.html to use Swagger UI. It explains each endpoint in plain English and lets you test calls from the browser.

📸 Screenshots: Capture your execution results from Swagger UI or your client app to share success stories with stakeholders.

🔧 Technical Details
Architecture Overview
CodeRank Executor follows a layered architecture:

flowchart LR
    Client[Clients<br/>(Web apps, LMS, IDE tools)] -->|HTTPS| Gateway[REST API Layer]
    Gateway -->|JWT validation| Security[Security Filters<br/>(JWT, Rate Limit, Request ID)]
    Security --> Services[Domain Services<br/>(Auth, Language, Execute, Admin)]
    Services -->|Read/Write| Database[(PostgreSQL<br/>Users, Languages, Submissions)]
    Services --> Orchestrator[Execution Orchestrator<br/>(Thread pool + Semaphores)]
    Orchestrator --> Queue[Bounded Queue<br/>Fair scheduling]
    Orchestrator --> Docker[Docker Engine<br/>Language Containers]
    Services --> Metrics[Micrometer Metrics<br/>Prometheus + Actuator]
    Gateway --> Logs[Structured Logging<br/>Request/Rate data]
Diagram description: Requests flow through security filters, then into services that either query PostgreSQL or dispatch execution tasks. The orchestrator enforces concurrency limits before invoking Docker to run the language-specific container. Metrics and logs are emitted at each step.

Technologies Used
Spring Boot 3.5.5: Provides the web server, dependency injection, and configuration management.

Spring Web (Spring MVC): Implements REST controllers for the API.

Spring Data JPA (Java Persistence API): Simplifies database access and mapping entities to PostgreSQL tables.

Flyway: Automatically applies database migrations on startup.

Spring Security + JWT: Delivers stateless authentication using JSON Web Tokens signed with HS256.

Micrometer + Prometheus: Collects runtime metrics for dashboards and alerts.

Docker: Runs untrusted code in isolated containers with strict resource limits.

Project Structure
coderank-executor/
├── src/main/java/com/coderank/executor/   # Application packages
│   ├── auth/       # Authentication controllers, DTOs, and services
│   ├── execute/    # Execution pipeline, Docker runner, concurrency controls
│   ├── language/   # Language catalogue endpoints and repositories
│   ├── admin/      # Admin-only controllers for languages and submissions
│   ├── submit/     # Submission entity and repository
│   ├── security/   # JWT filters, security configuration, password encoder
│   └── web/        # Filters, exception handling, health probe
├── src/main/resources/                    # Configuration, Flyway migrations, logging
├── languages/                             # Dockerfiles for language sandboxes
├── docker-compose.yml                     # Local orchestration for API + PostgreSQL
└── Dockerfile                             # Multi-stage build for the API container
🧱 Data Model (ER Diagram)
erDiagram
    USERS ||--o{ SUBMISSIONS : "makes"
    LANGUAGES ||--o{ SUBMISSIONS : "describes"
    USERS {
        uuid id PK
        string email
        string password_hash
        string role
        timestamp created_at
    }
    LANGUAGES {
        varchar code PK
        string display_name
        string image
        string file_name
        string compile_cmd
        string run_cmd
        string version
        boolean enabled
    }
    SUBMISSIONS {
        uuid id PK
        uuid user_id FK
        varchar language_code FK
        text source_code
        text stdin
        text stdout
        text stderr
        string status
        integer exec_time_ms
        integer memory_kb
        timestamp created_at
    }
Diagram description: Users optionally create many submissions; each submission references exactly one language definition. Deleting a language is restricted because submissions depend on it.

⚙️ Advanced Usage
Configuration overrides
Set environment variables or properties (e.g., APP_EXEC_TIMEOUTSECONDS=10) to adjust sandbox limits without code changes.

Custom language images
Add a new folder under languages/, provide a Dockerfile that writes to /sandbox, and seed the language via Flyway or the admin API.

Integration tips

Cache JWT tokens on the client until expiresInSeconds elapses.

Use the X-Request-Id header to trace calls across services.

Monitor coderank.execute.requests and coderank.execute.time Prometheus metrics to scale infrastructure proactively.

Admin APIs
Use /api/admin/languages to enable/disable languages at runtime and /api/admin/submissions to review the latest runs. These endpoints require the ADMIN role.

🧪 Testing This Project (Step-by-Step)
Prepare the environment

docker compose up -d db
export SPRING_PROFILES_ACTIVE=dev
export CODERANK_JWT_SECRET="$(printf 'MySuperSecretKey12345678901234567890' | base64)"
Sets up PostgreSQL and configures the JWT secret for tests.

Run unit and integration tests

./mvnw clean test
Verifies the Spring context loads and validates configuration wiring.

Execute full build with checks

./mvnw verify
Runs tests and produces the executable JAR to ensure the project is buildable.

Smoke-test the running service

./mvnw spring-boot:run
curl http://localhost:8080/api/health
Confirms the API starts and responds before you stop it with Ctrl+C.

Manual API walkthrough (recommended)

Register, log in, and execute a sample as shown in How to Use.

Observe rate-limit headers X-RateLimit-Limit and X-RateLimit-Remaining to confirm throttling.

Check logs to see request IDs and user context.

Docker-based end-to-end test

docker compose down -v
docker compose up --build
Ensures the container image, Docker socket mount, and database work together.

✅ Tip: Automate steps 1–4 in your CI pipeline to maintain confidence before deploying.

🤝 Contributing
We welcome contributions! Here's how you can help:

For New Contributors
Report bugs: Found something broken? Open an issue.

Suggest features: Have an idea? Start a discussion.

Improve docs: See unclear instructions? Submit a fix via pull request!

For Developers
Fork the repository.

Create your feature branch (git checkout -b feature/amazing-feature).

Make and document your changes.

Add or update tests where practical.

Ensure ./mvnw verify passes.

Submit a pull request with a clear summary and screenshots/recordings if UI or API behavior changed.

Detailed guidelines live in CONTRIBUTING.md (coming soon). Until then, mimic existing code style and add meaningful logging where appropriate.

💬 Support and Community
Documentation: Swagger UI (/swagger-ui/index.html) and this README cover most needs.

Issue tracker: Use GitHub Issues for bugs and feature requests.

Community chat: Join the #coderank-executor Slack channel (request access via the maintainers).

Email: Reach the platform team at platform@coderank.example.

FAQ snippets: See below for quick answers to recurring questions.

Frequently Asked Questions
Do I need Docker in production? Yes. The service relies on the Docker socket to spin up sandbox containers.

Can anonymous users run code? Yes, but they are rate limited by IP address instead of user ID.

How do I add a new language? Build a Docker image that writes source to /sandbox, add a row to the languages table, and enable it via the admin API.

Where are metrics exposed? Prometheus-compatible metrics live at /actuator/prometheus (authentication required).

🛠 Troubleshooting
JWT errors (401 Unauthorized): Ensure the Authorization: Bearer <token> header is present and the JWT secret matches across services.

429 Too Many Requests: Slow down calls or increase app.ratelimit.execute.perMinute in configuration.

System busy responses: Execution queue is full. Scale Docker resources or raise app.exec.concurrent.maxConcurrent.

Docker permission denied: On Linux, add your user to the docker group or run the API inside Docker with the socket mounted (/var/run/docker.sock).

Flyway migration failures: Drop and recreate the coderank database if the schema diverged during development.

📋 Project Details
Status: Active Development

Version: 0.0.1-SNAPSHOT

License: Proprietary / TBD (contact maintainers for terms)

Maintainers: CodeRank Platform Team (platform@coderank.example)

Roadmap
✅ Secure execution with Python, Java, and C++ sandboxes.

✅ JWT authentication, rate limiting, and audit logging.

🚧 Expand automated test coverage and add CI pipelines.

📋 Planned: WebSocket streaming outputs, configurable per-tenant quotas, and granular language permissions.

🧭 Maintenance & Staying Informed
Update cadence: Dependencies reviewed quarterly; security fixes released as soon as they’re validated.

Release notes: Follow GitHub Releases for version highlights.

Notifications: Watch the repository to get alerts for new features and fixes.

Backup guidance: Schedule regular PostgreSQL dumps; submissions and language data are critical for audits.

📊 Success Metrics & Default Limits
Throughput: Supports 6 concurrent executions and a queue of 20 jobs by default.

Rate limit: 30 code executions per user (or IP) per minute to keep the service responsive.

Timeouts: 5-second language timeout, 12-second overall Docker timeout, preventing runaway code.

Observability: Every request logs a requestId, user email (if authenticated), duration, and rate-limit usage to simplify incident response.

