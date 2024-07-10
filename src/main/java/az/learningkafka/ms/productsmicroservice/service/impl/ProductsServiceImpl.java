package az.learningkafka.ms.productsmicroservice.service.impl;

import az.kafka.kafkaeventslibrary.ProductCreatedEvent;
import az.learningkafka.ms.productsmicroservice.dto.ProductDto;
import az.learningkafka.ms.productsmicroservice.exception.KafkaSendException;
import az.learningkafka.ms.productsmicroservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static az.learningkafka.ms.productsmicroservice.shared.Constants.PRODUCT_CREATED_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsServiceImpl implements ProductService {
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Override
    public String createProductAsync(ProductDto productDto) {
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent newProductCreatedEvent = createNewProductCreatedEvent(productDto, productId);
        var futureResult = kafkaTemplate.send(PRODUCT_CREATED_TOPIC, productId, newProductCreatedEvent);
        futureResult.whenComplete((result, ex) ->
                log.info("Result : {}", ObjectUtils.isNotEmpty(ex) ? ex.getMessage()
                        : result.getRecordMetadata().toString()));
        return productId;
    }

    @Override
    public String createProductSync(ProductDto productDto) {
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent newProductCreatedEvent = createNewProductCreatedEvent(productDto, productId);
        try {
            var sendResult = kafkaTemplate.send(PRODUCT_CREATED_TOPIC, productId, newProductCreatedEvent).get();
            log.info("Topic : {}", sendResult.getRecordMetadata().topic());
            log.info("Partition : {}", sendResult.getRecordMetadata().partition());
            log.info("Offset : {}", sendResult.getRecordMetadata().offset());
            log.info("Time : {}", sendResult.getRecordMetadata().timestamp());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendException(e.getMessage());
        }
        return productId;
    }

    private ProductCreatedEvent createNewProductCreatedEvent(ProductDto productDto, String productId) {
        return ProductCreatedEvent.builder()
                .productId(productId)
                .title(productDto.title())
                .price(productDto.price())
                .quantity(productDto.quantity())
                .build();
    }
}
