package com.coderank.executor.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;


@Service
public class JwtService {
    private final String issuer;
    private final long expiryMinutes;
    private final Key key;


    public JwtService(
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.expiryMinutes}") long expiryMinutes,
            @Value("${app.security.jwt.secret}") String secretBase64
    ) {
        this.issuer = issuer;
        this.expiryMinutes = expiryMinutes;
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {
        return parseAllClaims(token).getBody().getSubject();
    }


    public boolean isTokenValid(String token, UserDetails user) {
        try {
            Jws<Claims> jws = parseAllClaims(token);
            String subject = jws.getBody().getSubject();
            Date exp = jws.getBody().getExpiration();
            return subject != null && subject.equals(user.getUsername()) && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public String generateToken(UserDetails user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryMinutes * 60_000);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    private Jws<Claims> parseAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
