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



```mermaid
erDiagram
  USER {
    UUID id PK
    string email UNIQUE
    string password_hash
    string role USER_OR_ADMIN
    timestamp created_at
  }

  EXECLANGUAGE {
    string code PK
    string display_name
    string image docker_image
    string file_name source_file
    string compile_cmd optional
    string run_cmd
    string version
    boolean enabled
  }

  SUBMISSION {
    UUID id PK
    UUID user_id FK
    string language_code FK
    text source
    text stdin
    text stdout
    text stderr
    string status
    int exec_time_ms
    int memory_kb
    timestamp created_at
  }

  USER ||--o{ SUBMISSION : "has many"
  EXECLANGUAGE ||--o{ SUBMISSION : "used by"
