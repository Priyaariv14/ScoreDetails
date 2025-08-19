package com.cricket.details.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SensitiveDataMaskingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SensitiveDataMaskingInterceptor.class);

    private final Set<String> sensitiveFields;

    public SensitiveDataMaskingInterceptor(
            @Value("${app.logging.sensitiveFields}") List<String> sensitiveFields) {
        this.sensitiveFields = sensitiveFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Map<String, String[]> requestParam = request.getParameterMap();
        Map<String, Object> sanitizedParam = new HashMap<>();

        requestParam.forEach((key, value) -> {
            if (sensitiveFields.contains(key.toLowerCase())) {
                sanitizedParam.put(key, "*************");
            } else {
                sanitizedParam.put(key, value.toString());
            }
        });

        log.info("Incoming Request: method={} uri={} params={}", request.getMethod(),
                request.getRequestURI(),
                sanitizedParam);
        return true;
    }
}
