package com.n11.marketplace.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.n11.marketplace.dto.response.MessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void businessExceptionShouldReturnItsStatusAndMessage() {
        BusinessException exception = new BusinessException("Invalid request", HttpStatus.BAD_REQUEST);

        ResponseEntity<MessageResponse> response = handler.handleBusinessException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void mailExceptionShouldReturnServiceUnavailable() {
        ResponseEntity<MessageResponse> response = handler.handleMailException();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Email service unavailable. Please try again later.", response.getBody().getMessage());
    }
}
