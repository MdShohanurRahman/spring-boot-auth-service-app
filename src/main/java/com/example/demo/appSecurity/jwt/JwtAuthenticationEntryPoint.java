/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.jwt;

import com.example.demo.models.ErrorModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        String expMessage = request.getAttribute("exp") == null ? "UnAuthorized" : (String) request.getAttribute("exp");
        log.error("Unauthorized error: {}", authenticationException.getMessage());
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorModel model = ErrorModel.builder().status(HttpStatus.UNAUTHORIZED.value()).message(expMessage).build();
        response.getWriter().write(convertObjectToJson(model.response()));
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
