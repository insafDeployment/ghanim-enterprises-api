package lk.ghanim.api.controller;

import lk.ghanim.api.dto.response.ProductResponse;
import lk.ghanim.api.service.SmartSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SmartSearchService smartSearchService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> smartSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                smartSearchService.smartSearch(query, limit)
        );
    }

    // Admin only — generate embeddings for all products
    @PostMapping("/generate-embeddings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateEmbeddings() {
        smartSearchService.generateEmbeddingsForAllProducts();
        return ResponseEntity.ok(
                "Embeddings generated successfully"
        );
    }
}