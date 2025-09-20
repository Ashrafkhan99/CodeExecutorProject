package com.coderank.executor.submit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId; // nullable (public run allowed)

    @Column(name = "language_code", nullable = false, length = 32)
    private String languageCode;

    // Use TEXT columns to match Flyway schema (avoid @Lob which maps to OID/CLOB in PG)
    @Column(name = "source_code", nullable = false, columnDefinition = "text")
    private String sourceCode;

    @Column(name = "stdin", columnDefinition = "text")
    private String stdin;

    @Column(name = "stdout", columnDefinition = "text")
    private String stdout;

    @Column(name = "stderr", columnDefinition = "text")
    private String stderr;

    @Column(nullable = false, length = 32)
    private String status; // SUCCESS / COMPILE_ERROR / RUNTIME_ERROR / TIMEOUT / INTERNAL_ERROR

    @Column(name = "exec_time_ms")
    private Integer execTimeMs;

    @Column(name = "memory_kb")
    private Integer memoryKb;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = Instant.now(); }

    // Getters & setters
    public UUID getId() { return id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

    public String getStdin() { return stdin; }
    public void setStdin(String stdin) { this.stdin = stdin; }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getExecTimeMs() { return execTimeMs; }
    public void setExecTimeMs(Integer execTimeMs) { this.execTimeMs = execTimeMs; }

    public Integer getMemoryKb() { return memoryKb; }
    public void setMemoryKb(Integer memoryKb) { this.memoryKb = memoryKb; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
