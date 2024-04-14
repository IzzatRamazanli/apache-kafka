package az.learningkafka.ms.productsmicroservice.handler;

import az.learningkafka.ms.productsmicroservice.dto.ErrorResponseDto;
import az.learningkafka.ms.productsmicroservice.exception.KafkaSendException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(KafkaSendException.class)
    protected ResponseEntity<Object> handleKafkaSendException(KafkaSendException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(ex, request, HttpStatus.BAD_REQUEST));
    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllPossibleException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(getErrorResponseDto(ex, request, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponseDto getErrorResponseDto(Exception ex, HttpServletRequest request,
                                                 HttpStatus status) {
        log.error("Exception occurred {}", ex.getMessage());
        return ErrorResponseDto.builder()
                .status(status.toString())
                .message(ex.getMessage())
                .requestUrl(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
