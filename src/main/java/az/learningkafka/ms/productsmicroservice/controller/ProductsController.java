package az.learningkafka.ms.productsmicroservice.controller;

import az.learningkafka.ms.productsmicroservice.dto.ProductDto;
import az.learningkafka.ms.productsmicroservice.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductsController {
    private final ProductService productService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping("/async")
    public ResponseEntity<String> createProductAsync(@RequestBody ProductDto productDto) {
        log.info("Request accepted on creating product {}", httpServletRequest.getRequestURI());
        String productId = productService.createProductAsync(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).eTag("productId").body(productId);
    }
    @PostMapping("/sync")
    public ResponseEntity<String> createProductSync(@RequestBody ProductDto productDto) throws Exception {
        log.info("Request accepted on creating product {}", httpServletRequest.getRequestURI());
        String productId = productService.createProductSync(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).eTag("productId").body(productId);
    }
}
