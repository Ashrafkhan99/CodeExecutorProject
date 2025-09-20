package com.coderank.executor.language;


import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class LanguageService {
    private final ExecLanguageRepository repo;
    public LanguageService(ExecLanguageRepository repo) { this.repo = repo; }


    public List<ExecLanguage> listEnabled() { return repo.findAllByEnabledTrueOrderByDisplayNameAsc(); }
}