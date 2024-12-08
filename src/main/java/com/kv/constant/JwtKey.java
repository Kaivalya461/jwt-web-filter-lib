package com.kv.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtKey {
    public static String JWT_SECRET;

    private JwtKey(@Value("${jwt.secret}") String jwtSecret) {
        JWT_SECRET = jwtSecret;
    }
}
