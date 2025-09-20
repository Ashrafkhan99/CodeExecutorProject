package com.coderank.executor.execute;

import com.coderank.executor.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execute")
public class ExecuteController {
    private final ExecuteService service;

    public ExecuteController(ExecuteService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ExecuteResponse> execute(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody ExecuteRequest req) {
        return ResponseEntity.ok(service.execute(user, req));
    }
}
