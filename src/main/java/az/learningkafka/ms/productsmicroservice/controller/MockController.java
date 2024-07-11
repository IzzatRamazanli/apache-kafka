package az.learningkafka.ms.productsmicroservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("status")
public class MockController {
    @GetMapping("/200")
    public ResponseEntity<String> get200Response() {
        return ResponseEntity.ok("200");
    }

    @GetMapping("/500")
    public ResponseEntity<String> get500Response() {
        return ResponseEntity.internalServerError().build();
    }
}
