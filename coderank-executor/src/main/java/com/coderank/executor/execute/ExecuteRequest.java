package com.coderank.executor.execute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ExecuteRequest {
    @NotBlank
    @Pattern(regexp = "^[a-z]{2,16}$", message = "language must be lowercase a-z, 2..16 chars")
    private String language;

    @NotBlank
    @Size(max = 20000)
    private String source;

    @Size(max = 10000)
    private String stdin;

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStdin() { return stdin; }
    public void setStdin(String stdin) { this.stdin = stdin; }
}
