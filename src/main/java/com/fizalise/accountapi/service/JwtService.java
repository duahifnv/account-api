package com.fizalise.accountapi.service;

import com.fizalise.accountapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j(topic = "Сервис управления токенами")
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.duration}")
    private Duration tokenLifetime;

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> extractFunction) {
        Claims claims = getAllClaims(token);
        return extractFunction.apply(claims);
    }

    private Claims getAllClaims(String token) {
        var payload = Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getPayload();
        log.info("Токен {}... прошел валидацию", token.substring(0, 10));
        return payload;
    }

    public String generateToken(User user) {
        // Параметры токена
        Map<String, Object> claims = new HashMap<>();
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + tokenLifetime.toMillis());
        // Собираем токен
        String generatedToken = Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        log.info("Токен для {} сгенерирован", user);
        return generatedToken;
    }
}
