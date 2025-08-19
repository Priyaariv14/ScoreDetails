package com.cricket.details.filter;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    // Add the fields you want to mask
    private static final String[] SENSITIVE_HEADERS = { "authorization" };
    private static final String[] SENSITIVE_PARAMS = { "password", "token", "ssn" };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        StringBuilder stringBuilder = new StringBuilder("{");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            if (isSensitive(name)) {
                value = "****";
            }
            stringBuilder.append(name).append("=").append(value).append(",");

        }
        stringBuilder.append("}");

        // Mask query params
        StringBuilder params = new StringBuilder("{");
        request.getParameterMap().forEach((key, values) -> {
            String value = String.join(",", values);
            if (isSensitive(value)) {
                value = "*****";
            }
            params.append(key).append("=").append(value).append(",");
        });

        params.append("}");

        // log.info("Request: method={}, uri={}, headers={}, params={}",
        // request.getMethod(), request.getRequestURI(), headers, params);
        filterChain.doFilter(request, response);
    }

    public boolean isSensitive(String input) {
        for (String s : SENSITIVE_HEADERS) {
            if (s.equals(input))
                return true;
        }
        for (String s : SENSITIVE_PARAMS) {
            if (s.equals(input))
                return true;
        }
        return false;
    }

}
