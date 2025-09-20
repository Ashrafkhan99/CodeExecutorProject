package com.coderank.executor.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateLanguageRequest {
    @Pattern(regexp = "^[a-z]{2,16}$", message = "code must be lowercase a-z, 2..16 chars")
    private String code;            // optional: allow rename in future (not used here)

    @Size(max = 64)
    private String displayName;

    @Size(max = 255)
    private String image;

    @Size(max = 64)
    private String fileName;

    @Size(max = 255)
    private String compileCmd;

    @Size(max = 255)
    private String runCmd;

    @Size(max = 32)
    private String version;

    private Boolean enabled;

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
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
