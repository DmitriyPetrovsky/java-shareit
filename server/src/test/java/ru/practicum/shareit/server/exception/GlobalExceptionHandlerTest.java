package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleValidationException_shouldReturnBadRequestWithFieldError() throws Exception {
        BindException bindException = mock(BindException.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(bindException.getFieldErrors()).thenReturn(List.of(fieldError));
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(bindException);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Поле field default message");
    }

    @Test
    void handleValidationException_whenNoFieldErrors_shouldThrowIllegalArgumentException() {
        BindException bindException = mock(BindException.class);
        when(bindException.getFieldErrors()).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class,
                () -> exceptionHandler.handleValidationException(bindException));
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Not found message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError()).isEqualTo("Not found message");
    }

    @Test
    void handleDoubleEmailException_shouldReturnConflict() {
        DoubleEmailException exception = new DoubleEmailException("Email conflict message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDoubleEmail(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("Email conflict message");
    }

    @Test
    void handleWrongUserException_shouldReturnConflict() {
        WrongUserException exception = new WrongUserException("Wrong user message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWrongUser(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("Wrong user message");
    }

    @Test
    void handleForbiddenUserException_shouldReturnForbidden() {
        ForbiddenUserException exception = new ForbiddenUserException("Forbidden message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleForbiddenUser(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getError()).isEqualTo("Forbidden message");
    }

    @Test
    void handleBadRequestException_shouldReturnBadRequest() {
        BadRequestException exception = new BadRequestException("Bad request message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWrongData(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Bad request message");
    }

    @Test
    void handleWrongDateException_shouldReturnBadRequest() {
        WrongDateException exception = new WrongDateException("Wrong date message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleWrongDate(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Wrong date message");
    }

    @Test
    void handleUnavailableItemException_shouldReturnBadRequest() {
        UnavailableItemException exception = new UnavailableItemException("Unavailable item message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnavailableItem(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Unavailable item message");
    }

    @Test
    void handleThrowable_shouldReturnInternalServerError() {
        Throwable throwable = new Throwable("Internal error message");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleThrowable(throwable);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError()).isEqualTo("Internal error message");
    }
}