package lk.ghanim.api.service;

import jakarta.transaction.Transactional;
import lk.ghanim.api.dto.response.ProductResponse;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmartSearchService {

    private final EmbeddingService embeddingService;
    private final ProductSearchRepository searchRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final JdbcTemplate jdbcTemplate;

    public List<ProductResponse> smartSearch(
            String query, int limit) {

        log.info("Smart search for: {}", query);

        // Generate embedding for the search query
        float[] queryEmbedding =
                embeddingService.generateEmbedding(query);

        if (queryEmbedding == null) {
            log.warn("Failed to generate embedding, " +
                    "falling back to keyword search");
            return productService.searchProducts(query);
        }

        // Find similar product IDs using pgvector
        List<Long> similarIds =
                searchRepository.findSimilarProductIds(
                        queryEmbedding, limit
                );

        if (similarIds.isEmpty()) {
            return productService.searchProducts(query);
        }

        // Fetch full product details
        return similarIds.stream()
                .map(id -> productRepository.findById(id))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .filter(Product::isActive)
                .map(product ->
                        productService.toResponse(product)
                )
                .collect(Collectors.toList());
    }

    public void generateEmbeddingForProduct(Product product) {
        String text = embeddingService.buildProductText(
                product.getName(),
                product.getDescription(),
                product.getCategory().getName()
        );

        float[] embedding =
                embeddingService.generateEmbedding(text);

        if (embedding != null) {
            searchRepository.updateProductEmbedding(
                    product.getId(), embedding
            );
            log.info("Embedding generated for product: {}",
                    product.getName());
        }
    }


    @Transactional
    public void generateEmbeddingsForAllProducts() {
        // Get IDs of products without embeddings from DB directly
        List<Long> productIdsWithoutEmbedding = jdbcTemplate.queryForList(
                "SELECT id FROM products WHERE embedding IS NULL AND active = true",
                Long.class
        );

        log.info("Generating embeddings for {} products",
                productIdsWithoutEmbedding.size());

        if (productIdsWithoutEmbedding.isEmpty()) {
            log.info("All products already have embeddings");
            return;
        }

        // Warm up model
        log.info("Warming up HuggingFace model...");
        embeddingService.generateEmbedding("warmup");
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        productIdsWithoutEmbedding.forEach(id -> {
            productRepository.findById(id).ifPresent(product -> {

                boolean success = false;

                for (int attempt = 1; attempt <= 3; attempt++) {
                    float[] embedding = embeddingService.generateEmbedding(
                            embeddingService.buildProductText(
                                    product.getName(),
                                    product.getDescription(),
                                    product.getCategory().getName()
                            )
                    );

                    if (embedding != null) {
                        searchRepository.updateProductEmbedding(
                                product.getId(), embedding
                        );
                        log.info("✅ Embedding saved for: {}",
                                product.getName());
                        success = true;
                        break;
                    }

                    log.warn("Attempt {} failed for: {}",
                            attempt, product.getName());
                    try {
                        Thread.sleep(2000L * attempt);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (!success) {
                    log.error("❌ All attempts failed for: {}",
                            product.getName());
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        log.info("✅ Embedding generation complete");
    }
}
