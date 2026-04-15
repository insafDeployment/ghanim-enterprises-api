package lk.ghanim.api.service;

import lk.ghanim.api.entity.Category;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.exception.ResourceNotFoundException;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.request.ProductRequest;
import lk.ghanim.api.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<ProductResponse> getAllProducts(){
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(String slug) {
        return productRepository
                .findByCategorySlugAndActiveTrue(slug)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));
        return toResponse(product);
    }

    public List<ProductResponse> searchProducts(String query) {
        return productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryService
                .getCategoryById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .retailPrice(request.getRetailPrice())
                .wholesalePrice(request.getWholesalePrice())
                .stock(request.getStock())
                .emoji(request.getEmoji())
                .imageUrl(request.getImageUrl())
                .badge(request.getBadge() != null
                        ? Product.Badge.valueOf(request.getBadge())
                        : null)
                .category(category)
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(
            Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        Category category = categoryService
                .getCategoryById(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setRetailPrice(request.getRetailPrice());
        product.setWholesalePrice(request.getWholesalePrice());
        product.setStock(request.getStock());
        product.setEmoji(request.getEmoji());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        if (request.getBadge() != null) {
            product.setBadge(Product.Badge.valueOf(request.getBadge()));
        }

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));
        product.setActive(false);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        boolean isWholesale = isCurrentUserWholesale();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(isWholesale
                ? product.getWholesalePrice(): product.getRetailPrice())
                .stock(product.getStock())
                .emoji(product.getEmoji())
                .imageUrl(product.getImageUrl())
                .badge(product.getBadge() != null ? product.getBadge().name() : null)
                .categoryName(product.getCategory().getName())
                .categorySlug(product.getCategory().getSlug())
                .inStock(product.getStock() > 0)
                .build();

    }

    private boolean isCurrentUserWholesale() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_WHOLESALE") ||
                        a.getAuthority().equals("ROLE_ADMIN"));
    }
}
