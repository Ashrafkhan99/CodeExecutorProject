package com.coderank.executor.execute;

import com.coderank.executor.language.ExecLanguage;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class DockerRunner {
    private final ExecProperties props;

    public DockerRunner(ExecProperties props) {
        this.props = props;
    }

    public record Result(String stdout, String stderr, ExecStatus status, int execTimeMs) {}

    public Result run(ExecLanguage lang, String source, String stdin) throws IOException, InterruptedException {
        String codeB64 = Base64.getEncoder().encodeToString(source.getBytes(StandardCharsets.UTF_8));
        String stdinB64 = (stdin == null ? "" : Base64.getEncoder().encodeToString(stdin.getBytes(StandardCharsets.UTF_8)));
        String file = "/sandbox/" + lang.getFileName();
        String compile = (lang.getCompileCmd() == null ? "" : lang.getCompileCmd());
        String run = lang.getRunCmd();
        int runTimeout = props.getTimeoutSeconds();

        String cname = "coderank-run-" + UUID.randomUUID().toString().replace("-", "");

        // POSIX sh-compatible script (no 'pipefail')
        StringBuilder script = new StringBuilder();
        script.append("set -eu; ");
        // write source
        script.append("printf %s \"$CODE_B64\" | base64 -d > ").append(file).append("; ");
        // optional compile (exit 88 on compile error)
        if (!compile.isBlank()) {
            script.append("{ ").append(compile).append(" ; } > /sandbox/.c.out 2> /sandbox/.c.err || ")
                    .append("{ cat /sandbox/.c.out; cat /sandbox/.c.err 1>&2; exit 88; }; ");
        }
        // detect timeout and build run cmd
        script.append("if command -v timeout >/dev/null 2>&1; then TOUT=timeout; else TOUT=; fi; ")
                .append("RUN_CMD=\"${TOUT:+$TOUT ").append(runTimeout).append("s} ").append(run).append("\"; ");
        // execute with or without stdin
        script.append("if [ -n \"${STDIN_B64:-}\" ]; then printf %s \"$STDIN_B64\" | base64 -d | sh -lc \"$RUN_CMD\"; ")
                .append("else sh -lc \"$RUN_CMD\"; fi;");

        List<String> cmd = new ArrayList<>(List.of(
                "docker","run","--rm","--name",cname,
                "--network","none",
                "--cpus", String.valueOf(props.getCpus()),
                "--memory", props.getMemory(),
                "--pids-limit", String.valueOf(props.getPidsLimit()),
                "--read-only",
                "--tmpfs","/sandbox:rw,size="+props.getTmpfsSizeMb()+"m,exec,noatime,uid=10001,gid=10001,mode=0777",
                "--security-opt","no-new-privileges",
                "--cap-drop","ALL",
                "-u","10001:10001",
                "-e","CODE_B64="+codeB64
        ));
        if (!stdinB64.isEmpty()) { cmd.addAll(List.of("-e","STDIN_B64="+stdinB64)); }
        cmd.add(lang.getImage());
        cmd.addAll(List.of("sh","-lc", script.toString()));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);

        long startNs = System.nanoTime();
        Process p = pb.start();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> outF = pool.submit(() -> readAll(p.getInputStream()));
        Future<String> errF = pool.submit(() -> readAll(p.getErrorStream()));

        boolean finished = p.waitFor(props.getOverallTimeoutSeconds(), TimeUnit.SECONDS);
        int exit = -1;
        if (!finished) {
            runSilently(List.of("docker","rm","-f",cname));
            p.destroyForcibly();
        } else {
            exit = p.exitValue();
        }

        String stdout = safeGet(outF);
        String stderr = safeGet(errF);
        pool.shutdownNow();

        int elapsedMs = (int) Duration.ofNanos(System.nanoTime() - startNs).toMillis();
        ExecStatus status;
        if (!finished) {
            status = ExecStatus.TIMEOUT;
        } else if (exit == 0) {
            status = ExecStatus.SUCCESS;
        } else if (exit == 88) {
            status = ExecStatus.COMPILE_ERROR;
        } else if (exit == 124) {
            status = ExecStatus.TIMEOUT; // GNU timeout
        } else {
            status = ExecStatus.RUNTIME_ERROR;
        }
        return new Result(stdout, stderr, status, elapsedMs);
    }

    private static String readAll(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    private static String safeGet(Future<String> f) {
        try { return f.get(200, TimeUnit.MILLISECONDS); } catch (Exception e) { return ""; }
    }

    private static void runSilently(List<String> cmd) {
        try {
            Process p = new ProcessBuilder(cmd).start();
            p.waitFor(2, TimeUnit.SECONDS);
        } catch (Exception ignored) {}
    }
}
