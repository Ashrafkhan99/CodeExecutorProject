package com.coderank.executor.submit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {}
