package az.learningkafka.ms.productsmicroservice.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String PRODUCT_CREATED_TOPIC = "product-create-events-topic";
}
