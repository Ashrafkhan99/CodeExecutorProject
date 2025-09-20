package com.coderank.executor.admin;

import com.coderank.executor.admin.dto.UpdateLanguageRequest;
import com.coderank.executor.language.ExecLanguage;
import com.coderank.executor.language.ExecLanguageRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/languages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLanguageController {

    private final ExecLanguageRepository repo;

    public AdminLanguageController(ExecLanguageRepository repo) { this.repo = repo; }

    @GetMapping
    public ResponseEntity<List<ExecLanguage>> listAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PatchMapping("/{code}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String code,
                                                      @Valid @RequestBody UpdateLanguageRequest req) {
        ExecLanguage lang = repo.findById(code).orElseThrow(() -> new IllegalArgumentException("Language not found: " + code));
        if (req.getDisplayName() != null) lang.setDisplayName(req.getDisplayName());
        if (req.getImage() != null)       lang.setImage(req.getImage());
        if (req.getFileName() != null)    lang.setFileName(req.getFileName());
        if (req.getCompileCmd() != null)  lang.setCompileCmd(req.getCompileCmd());
        if (req.getRunCmd() != null)      lang.setRunCmd(req.getRunCmd());
        if (req.getVersion() != null)     lang.setVersion(req.getVersion());
        if (req.getEnabled() != null)     lang.setEnabled(req.getEnabled());
        repo.save(lang);
        return ResponseEntity.ok(Map.of(
                "code", lang.getCode(),
                "enabled", lang.isEnabled(),
                "image", lang.getImage(),
                "version", lang.getVersion()
        ));
    }
}
