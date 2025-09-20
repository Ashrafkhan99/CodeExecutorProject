```mermaid
flowchart LR
  Client[Client (UI / API Consumer)]
  API[Spring Boot API\n(Controllers, Filters, Services)]
  DB[(PostgreSQL\nUsers, Languages, Submissions)]
  Docker[Docker Engine\nSandbox Containers]
  Metrics[(Prometheus / Actuator)]
  Flyway[Flyway\n(DB migrations)]

  Client -->|REST JSON| API
  API --> DB
  API --> Docker
  API --> Metrics
  API -.startup.-> Flyway
