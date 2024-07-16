package az.learningkafka.ms.productsmicroservice;

import az.learningkafka.ms.productsmicroservice.dto.ProductDto;
import az.learningkafka.ms.productsmicroservice.events.ProductCreatedEvent;
import az.learningkafka.ms.productsmicroservice.service.ProductService;
import az.learningkafka.ms.productsmicroservice.shared.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(count = 3, partitions = 3, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
class ProductProducerIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, Object> kafkaListenerContainerFactory;
    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> queue;

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, Object> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfigs());
        ContainerProperties containerProperties = new ContainerProperties(Constants.PRODUCT_CREATED_TOPIC);
        kafkaListenerContainerFactory = new KafkaMessageListenerContainer<>(factory, containerProperties);
        queue = new LinkedBlockingQueue<>();
        kafkaListenerContainerFactory.setupMessageListener((MessageListener<String, ProductCreatedEvent>) queue::add);
        kafkaListenerContainerFactory.start();
        ContainerTestUtils.waitForAssignment(kafkaListenerContainerFactory, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        kafkaListenerContainerFactory.stop();
    }


    @Test
    void testProductCreatedEvent_withValidProductDetails_sendMessageToKafkaSuccess() throws InterruptedException {
        ProductDto productRequest = getProductRequest();
        productService.createProductSync(productRequest);

        ConsumerRecord<String, ProductCreatedEvent> message = queue.poll(3000, TimeUnit.MILLISECONDS);

        Assertions.assertNotNull(message);
        Assertions.assertNotNull(message.key());
        ProductCreatedEvent value = message.value();
        Assertions.assertEquals(productRequest.price(), value.getPrice());
    }

    private static ProductDto getProductRequest() {
        return ProductDto.builder()
                .title("iphone")
                .price(BigDecimal.valueOf(3000))
                .quantity(1)
                .build();
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, ProductCreatedEvent.class,
                ConsumerConfig.GROUP_ID_CONFIG, "product-created-events",
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
