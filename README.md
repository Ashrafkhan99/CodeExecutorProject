## System Flowchart
```mermaid
flowchart LR
  Client[Client: UI or API Consumer]
  API[Spring Boot API: Controllers, Filters, Services]
  DB[(PostgreSQL: Users, Languages, Submissions)]
  Docker[Docker Engine: Sandbox Containers]
  Metrics[(Prometheus / Actuator)]
  Flyway[Flyway: DB migrations]
  Client -->|REST JSON| API
  API --> DB
  API --> Docker
  API --> Metrics
  API -.startup.-> Flyway
