package az.learningkafka.ms.productsmicroservice.service;

import az.learningkafka.ms.productsmicroservice.dto.ProductDto;

public interface ProductService {
    String createProductAsync(ProductDto productDto);
    String createProductSync(ProductDto productDto);

}
