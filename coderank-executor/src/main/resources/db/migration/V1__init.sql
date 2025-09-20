-- Users
CREATE TABLE users (
                       id UUID NOT NULL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(32) NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- Languages (code acts as primary key for simplicity)
CREATE TABLE languages (
                           code VARCHAR(32) PRIMARY KEY, -- e.g. 'python', 'java', 'cpp'
                           display_name VARCHAR(64) NOT NULL,
                           image VARCHAR(255) NOT NULL, -- docker image tag
                           file_name VARCHAR(64) NOT NULL, -- source file name inside sandbox
                           compile_cmd TEXT, -- optional (e.g., javac, g++)
                           run_cmd TEXT NOT NULL, -- interpreter or binary run
                           version VARCHAR(32),
                           enabled BOOLEAN NOT NULL DEFAULT TRUE
);


-- Submissions
CREATE TABLE submissions (
                             id UUID NOT NULL PRIMARY KEY,
                             user_id UUID, -- nullable until auth is added
                             language_code VARCHAR(32) NOT NULL REFERENCES languages(code),
                             source_code TEXT NOT NULL,
                             stdin TEXT,
                             stdout TEXT,
                             stderr TEXT,
                             status VARCHAR(32) NOT NULL, -- SUCCESS / COMPILE_ERROR / RUNTIME_ERROR / TIMEOUT
                             exec_time_ms INTEGER,
                             memory_kb INTEGER,
                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


-- Seed initial languages (images provided in Phase 3)
INSERT INTO languages (code, display_name, image, file_name, compile_cmd, run_cmd, version, enabled) 
VALUES
    ('python', 'Python 3.11', 'coderank/lang-python:3.11', 'main.py', NULL, 'python3 /sandbox/main.py', '3.11', TRUE),
    ('java', 'Java 17', 'coderank/lang-java:17', 'Main.java', 'javac -J-Xms32m -J-Xmx256m /sandbox/Main.java', 'java -Xms32m -Xmx256m -cp /sandbox Main', '17', TRUE),
    ('cpp', 'C++17', 'coderank/lang-cpp:17', 'main.cpp', 'g++ -O2 -std=c++17 -o /sandbox/a.out /sandbox/main.cpp', '/sandbox/a.out', '17', TRUE);