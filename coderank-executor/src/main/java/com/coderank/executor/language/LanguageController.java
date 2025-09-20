package com.coderank.executor.language;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguageService service;
    public LanguageController(LanguageService service) { this.service = service; }


    @GetMapping
    public ResponseEntity<List<Map<String, String>>> list() {
        var out = service.listEnabled().stream().map(l -> Map.of(
                "code", l.getCode(),
                "name", l.getDisplayName(),
                "version", l.getVersion()
        )).toList();
        return ResponseEntity.ok(out);
    }
}
