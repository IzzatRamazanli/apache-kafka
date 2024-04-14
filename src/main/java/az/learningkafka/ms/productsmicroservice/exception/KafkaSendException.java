package az.learningkafka.ms.productsmicroservice.exception;

public class KafkaSendException extends RuntimeException{
    public KafkaSendException(String message) {
        super(message);
    }
}
