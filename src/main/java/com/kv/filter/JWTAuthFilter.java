package com.kv.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kv.dto.StandardError;
import com.kv.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Log4j2
public class JWTAuthFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Allow OPTIONS(CORS Preflight) requests to pass through this AuthFilter.
        if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        final String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                Claims claims = JWTUtil.parseToken(jwt);
                httpRequest.setAttribute("claims", claims);
                filterChain.doFilter(httpRequest, httpResponse); // Proceed if token is valid
            } catch (ExpiredJwtException expiredJwtException) {
                sendErrorResponse(HttpStatus.UNAUTHORIZED, "JWT token is expired", httpResponse);
            } catch (JwtException jwtException) {
                sendErrorResponse(HttpStatus.UNAUTHORIZED, "JWT token is invalid", httpResponse);
            }
        } else {
            sendErrorResponse(HttpStatus.UNAUTHORIZED, "Authorization header is missing or invalid", httpResponse);
        }
    }

    private void sendErrorResponse(HttpStatus httpStatus,
                                   String errorMessage,
                                   HttpServletResponse httpResponse
    ) throws IOException {
        httpResponse.setStatus(httpStatus.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        StandardError standardError = new StandardError(httpStatus.getReasonPhrase(), errorMessage);
        ObjectMapper objectmapper = new ObjectMapper();
        objectmapper.writeValue(httpResponse.getWriter(), standardError);
    }
}
