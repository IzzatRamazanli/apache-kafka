package az.learningkafka.ms.productsmicroservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponseDto(
        String status,
        String message,
        LocalDateTime timestamp,
        String requestUrl
) {
}
