package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.config.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handle404Error(NoResourceFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder(ex, HttpStatusCode.valueOf(404), "Page Not Found").build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
