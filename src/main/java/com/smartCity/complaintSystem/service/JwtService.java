package com.smartCity.complaintSystem.service;


import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {

	private final String SECRET = "bXl2ZXJ5c3Ryb25nc2VjcmV0a2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTA=";

	private Key getSignKey() {
	    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
	}

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 1 hr
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // ✅ FIXED
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) // ✅ FIXED
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}