erDiagram
  USER {
    UUID id PK
    string email "UNIQUE"
    string password_hash
    string role "USER|ADMIN"
    timestamp created_at
  }

  EXECLANGUAGE {
    string code PK "e.g. python|java|cpp"
    string display_name
    string image "docker image"
    string file_name "sandbox source file"
    string compile_cmd "nullable"
    string run_cmd
    string version
    boolean enabled
  }

  SUBMISSION {
    UUID id PK
    UUID user_id FK "nullable (anonymous allowed)"
    string language_code FK
    text source
    text stdin
    text stdout
    text stderr
    string status "SUCCESS|ERROR|TIMEOUT"
    int exec_time_ms
    int memory_kb
    timestamp created_at
  }

  USER ||--o{ SUBMISSION : "has many"
  EXECLANGUAGE ||--o{ SUBMISSION : "used by"
