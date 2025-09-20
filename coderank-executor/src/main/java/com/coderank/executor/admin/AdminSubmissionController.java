package com.coderank.executor.admin;

import com.coderank.executor.submit.Submission;
import com.coderank.executor.submit.SubmissionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/submissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubmissionController {

    private final SubmissionRepository submissions;

    public AdminSubmissionController(SubmissionRepository submissions) {
        this.submissions = submissions;
    }

    @GetMapping
    public ResponseEntity<List<Submission>> latest(@RequestParam(defaultValue = "20") int limit) {
        int safe = Math.min(Math.max(limit, 1), 200);
        var page = submissions.findAll(PageRequest.of(0, safe, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(page.getContent());
    }
}
