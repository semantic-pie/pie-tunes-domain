package com.apipietunes.clients.services.jwt;

import com.apipietunes.clients.models.UserSql;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @NonNull
    private final JwtTokenProperties jwtTokenProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        var secret = Base64.getEncoder()
                .encodeToString(jwtTokenProperties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String getUUID(String token) {
        return getAllClaimsFromToken(token).getBody().get("uuid", String.class);
    }

    public Jws<Claims> getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = getAllClaimsFromToken(token);
            // parseClaimsJws will check expiration date. No need do here.

            log.info("Expiration date: {}", claims.getBody().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getAllClaimsFromToken(token).getBody();

        Object authoritiesClaim = claims.get("roles");

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                authorities);
    }

    public String generateToken(UserSql user) {
        Map<String, Object> claims = new HashMap<>();

        // payload
        claims.put("uuid", user.getUuid());
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        var issuedDate = new Date();
        var expiredDate = new Date(issuedDate.getTime() + jwtTokenProperties.getJwtLifeTime().toMillis());

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        return Jwts.builder()
                .setSubject(user.getEmail()) // usually it's login
                .setClaims(claims)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(secretKey, signatureAlgorithm)
                .compact();
    }

    public String getJwtTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasLength(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }





}
