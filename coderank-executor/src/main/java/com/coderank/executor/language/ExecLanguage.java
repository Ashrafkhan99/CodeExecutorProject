package com.coderank.executor.language;


import jakarta.persistence.*;


@Entity
@Table(name = "languages")
public class ExecLanguage {
    @Id
    @Column(length = 32)
    private String code; // e.g., python, java, cpp


    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;


    @Column(nullable = false, length = 255)
    private String image; // docker image tag


    @Column(name = "file_name", nullable = false, length = 64)
    private String fileName; // main.py / Main.java / main.cpp


    @Column(name = "compile_cmd")
    private String compileCmd; // nullable


    @Column(name = "run_cmd", nullable = false)
    private String runCmd;


    @Column(length = 32)
    private String version;


    @Column(nullable = false)
    private boolean enabled = true;


    // Getters/setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getCompileCmd() { return compileCmd; }
    public void setCompileCmd(String compileCmd) { this.compileCmd = compileCmd; }
    public String getRunCmd() { return runCmd; }
    public void setRunCmd(String runCmd) { this.runCmd = runCmd; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
