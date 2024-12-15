package com.kv.util;

import com.kv.constant.JwtKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class JWTUtil {
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 1000 * 60 * 60); // 1 Hour
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 1000 * 60 * 60 * 24); // 1 Day
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationDate) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .signWith(getSignInKey()) //Do I need to pass HS256 Algo manually?
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationDate))
                .compact();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public Boolean validateToken(String token, String usernameFromDatabase) {
        final String usernameFromToken = extractUsername(token);
        return (usernameFromToken.equals(usernameFromDatabase) && !isTokenExpired(token));
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Base64.getDecoder()
                .decode(JwtKey.JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

      /*
      * Validation Process inside parseClaimsJws() method:
      *     1. Signature Validation
      *         If signature doesn't match, throws JwtException.
      *     2. Expiration Check
      *         If token is expired, throws ExpiredJwtException.
      *     3. Integrity Check
      *         Token's Payload cross-check with signature.
      * */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
