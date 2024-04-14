package az.learningkafka.ms.productsmicroservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(
        String title,
        BigDecimal price,
        Integer quantity
) {
}
