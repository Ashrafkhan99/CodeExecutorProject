erDiagram
  USER {
    UUID id PK
    string email "UNIQUE"
    string password_hash
    string role "USER|ADMIN"
    timestamp created_at "DEFAULT NOW()"
  }

  EXECLANGUAGE {
    string code PK "e.g. python|java|cpp"
    string display_name
    string image "docker image tag"
    string file_name "source file inside sandbox"
    string compile_cmd "nullable"
    string run_cmd
    string version
    boolean enabled
  }

  SUBMISSION {
    UUID id PK
    UUID user_id FK "nullable (anonymous allowed)"
    string language_code FK "references languages(code)"
    text source
    text stdin
    text stdout
    text stderr
    string status "SUCCESS|COMPILE_ERROR|RUNTIME_ERROR|TIMEOUT|INTERNAL_ERROR"
    int exec_time_ms
    int memory_kb
    timestamp created_at "DEFAULT NOW()"
  }

  USER ||--o{ SUBMISSION : "has many"
  EXECLANGUAGE ||--o{ SUBMISSION : "used by"
