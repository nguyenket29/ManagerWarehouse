package com.hau.warehouse.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class AuthenticationException implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationException.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, org.springframework.security.core.AuthenticationException e) throws IOException, ServletException {
        logger.error("Unauthoried error: " + e.getMessage());
        //httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized !");

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> set = new HashMap<>();
        set.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        set.put("error", "Unauthoried");
        set.put("message", e.getMessage());
        set.put("path", httpServletRequest.getServletPath());

        logger.info("HandlerAuthenticationException | commence | status: {}", HttpServletResponse.SC_UNAUTHORIZED);
        logger.info("HandlerAuthenticationException | commence | error: {}", "Unauthorized");
        logger.info("HandlerAuthenticationException | commence | message: {}", e.getMessage());
        logger.info("HandlerAuthenticationException | commence | path: {}", httpServletRequest.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(httpServletResponse.getOutputStream(), set);
    }
}
