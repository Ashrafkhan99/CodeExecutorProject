package com.coderank.executor.language;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ExecLanguageRepository extends JpaRepository<ExecLanguage, String> {
    List<ExecLanguage> findAllByEnabledTrueOrderByDisplayNameAsc();
}
