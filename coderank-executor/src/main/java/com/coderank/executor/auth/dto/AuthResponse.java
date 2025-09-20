package com.coderank.executor.auth.dto;


public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresInSeconds;


    public AuthResponse(String token, long expiresInSeconds) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
    }
    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}
