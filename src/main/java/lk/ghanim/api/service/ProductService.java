package lk.ghanim.api.service;

import jakarta.transaction.Transactional;
import lk.ghanim.api.entity.Category;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.entity.ProductImage;
import lk.ghanim.api.exception.ResourceNotFoundException;
import lk.ghanim.api.repository.ProductImageRepository;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.dto.request.ProductRequest;
import lk.ghanim.api.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;

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

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryService
                .getCategoryById(request.getCategoryId());

        String primaryUrl = null;

        if (request.getImageUrls() != null &&
        !request.getImageUrls().isEmpty()) {
            primaryUrl = request.getImageUrls()
                    .stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .findFirst()
                    .orElse(null);
        }
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .specifications(request.getSpecifications())
                .retailPrice(request.getRetailPrice())
                .wholesalePrice(request.getWholesalePrice())
                .costPrice(request.getCostPrice())
                .stock(request.getStock())
                .emoji(request.getEmoji())
                .imageUrl(primaryUrl)
                .images(new ArrayList<>())
                .badge(request.getBadge() != null
                        ? Product.Badge.valueOf(request.getBadge())
                        : null)
                .category(category)
                .active(true)
                .build();


        Product saved = productRepository.save(product);


        // save Images seperately

        if (request.getImageUrls() != null) {
            List<ProductImage> images = new ArrayList<>();
            List<String> filtered = request.getImageUrls()
                    .stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .collect(Collectors.toList());

            for (int i = 0; i < filtered.size(); i++) {
                images.add(ProductImage.builder()
                        .product(saved)
                                .imageUrl(filtered.get(i).trim())
                                .isPrimary(i == 0)
                                .sortOrder(i)
                        .build());
            }

            if (!images.isEmpty()) {
                productImageRepository.saveAll(images);
                saved.setImages(images);
            }
        }

        return toResponse(saved);
    }

    @Transactional
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
        product.setCostPrice(request.getCostPrice());
        product.setSpecifications(request.getSpecifications());
        product.setStock(request.getStock());
        product.setEmoji(request.getEmoji());
        product.setBadge(request.getBadge() != null && !request.getBadge().isEmpty()
                ? Product.Badge.valueOf(request.getBadge())
                : null);
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<String> filtered = request.getImageUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .collect(Collectors.toList());

            if (!filtered.isEmpty()) {
                product.setImageUrl(filtered.get(0));
                productImageRepository.deleteByProductId(product.getId());

                List<ProductImage> images = new ArrayList<>();
                for (int i = 0; i < filtered.size(); i++) {
                    images.add(ProductImage.builder()
                            .product(product)
                            .imageUrl(filtered.get(i).trim())
                            .isPrimary(i == 0)
                            .sortOrder(i)
                            .build());
                }
                productImageRepository.saveAll(images);
                product.setImages(images);
            }
        }
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

    public ProductResponse toResponse(Product product) {
        boolean isWholesale = isCurrentUserWholesale();

        // Safely handle null or empty images
        List<String> imageUrls = new ArrayList<>();

        try {
            if (product.getImages() != null &&
                    !product.getImages().isEmpty()) {
                imageUrls = product.getImages()
                        .stream()
                        .filter(img -> img != null &&
                                img.getImageUrl() != null)
                        .sorted(Comparator.comparingInt(
                                img -> img.getSortOrder() != null
                                        ? img.getSortOrder() : 999
                        ))
                        .map(ProductImage::getImageUrl)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Could not load images for product {}: {}",
                    product.getId(), e.getMessage());
        }

        // Fall back to single imageUrl if no images table entries
        if(imageUrls.isEmpty() && product.getImageUrl() != null) {
            imageUrls = List.of(product.getImageUrl());
        }
        String primaryImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .specifications(product.getSpecifications())
                .price(isWholesale
                ? product.getWholesalePrice(): product.getRetailPrice())
                .stock(product.getStock())
                .emoji(product.getEmoji())
                .imageUrl(primaryImageUrl)
                .imageUrls(imageUrls)
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

    private void saveProductImages(
            Product product, List<String> imageUrls
    ) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        productImageRepository.deleteByProductId(product.getId());

        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);

            if (url != null && !url.trim().isEmpty()) {
                images.add(ProductImage.builder()
                                .product(product)
                                .imageUrl(url.trim())
                                .isPrimary(i == 0)
                                .sortOrder(i)
                        .build());
            }
        }

        if (!images.isEmpty()) {
            productImageRepository.saveAll(images);
        }
    }
}


