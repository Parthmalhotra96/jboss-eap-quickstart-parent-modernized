package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.config.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handle404Error(NoResourceFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatusCode.valueOf(404), "Page Not Found").build();
        return "404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleForbiddenException(AccessDeniedException ex) {
        log.error("Forbidden access", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden access");
    }
}
