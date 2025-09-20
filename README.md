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


erDiagram
  USER ||--o{ SUBMISSION : has
  EXECLANGUAGE ||--o{ SUBMISSION : used_by

  USER {
    UUID id
    string email
    string password_hash
    string role
    timestamp created_at
  }

  EXECLANGUAGE {
    string code
    string display_name
    string image
    string file_name
    string compile_cmd
    string run_cmd
    string version
    boolean enabled
  }

  SUBMISSION {
    UUID id
    UUID user_id
    string language_code
    text source
    text stdin
    text stdout
    text stderr
    string status
    int exec_time_ms
    int memory_kb
    timestamp created_at
  }
```mermaid
