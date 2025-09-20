package com.coderank.executor.execute;

import com.coderank.executor.language.ExecLanguage;
import com.coderank.executor.language.ExecLanguageRepository;
import com.coderank.executor.submit.Submission;
import com.coderank.executor.submit.SubmissionRepository;
import com.coderank.executor.user.User;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class ExecuteService {
    private final ExecLanguageRepository languages;
    private final SubmissionRepository submissions;
    private final ExecutionOrchestrator orchestrator;
    private final MeterRegistry meters;

    public ExecuteService(ExecLanguageRepository languages,
                          SubmissionRepository submissions,
                          ExecutionOrchestrator orchestrator,
                          MeterRegistry meters) {
        this.languages = languages;
        this.submissions = submissions;
        this.orchestrator = orchestrator;
        this.meters = meters;
    }

    public ExecuteResponse execute(User user, ExecuteRequest req) {
        ExecLanguage lang = languages.findById(req.getLanguage())
                .filter(ExecLanguage::isEnabled)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported or disabled language: " + req.getLanguage()));

        DockerRunner.Result r;
        try {
            r = orchestrator.submitAndWait(user, lang, req.getSource(), Optional.ofNullable(req.getStdin()).orElse(""));
        } catch (Exception e) {
            recordMetrics(lang.getCode(), ExecStatus.INTERNAL_ERROR, 0);
            Submission s = new Submission();
            if (user != null) s.setUserId(user.getId());
            s.setLanguageCode(req.getLanguage());
            s.setSourceCode(req.getSource());
            s.setStdin(req.getStdin());
            s.setStdout("");
            s.setStderr(e.getClass().getSimpleName() + ": " + e.getMessage());
            s.setStatus(ExecStatus.INTERNAL_ERROR.name());
            s.setExecTimeMs(null);
            submissions.save(s);
            return new ExecuteResponse("", s.getStderr(), ExecStatus.INTERNAL_ERROR, 0);
        }

        recordMetrics(lang.getCode(), r.status(), r.execTimeMs());

        Submission s = new Submission();
        if (user != null) s.setUserId(user.getId());
        s.setLanguageCode(req.getLanguage());
        s.setSourceCode(req.getSource());
        s.setStdin(req.getStdin());
        s.setStdout(r.stdout());
        s.setStderr(r.stderr());
        s.setStatus(r.status().name());
        s.setExecTimeMs(r.execTimeMs());
        s.setMemoryKb(null);
        submissions.save(s);

        return new ExecuteResponse(r.stdout(), r.stderr(), r.status(), r.execTimeMs());
    }

    private void recordMetrics(String language, ExecStatus status, int execTimeMs) {
        meters.counter("coderank.execute.requests", "language", language, "status", status.name()).increment();
        if (execTimeMs > 0) {
            meters.timer("coderank.execute.time", "language", language, "status", status.name())
                    .record(Duration.ofMillis(execTimeMs));
        }
    }
}
