package lk.ghanim.api.controller;


import lk.ghanim.api.dto.response.AdminProductResponse;
import lk.ghanim.api.dto.response.DashboardResponse;
import lk.ghanim.api.dto.response.OrderResponse;
import lk.ghanim.api.dto.response.UserResponse;
import lk.ghanim.api.entity.ProductImage;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.UserRepository;
import lk.ghanim.api.service.DashboardService;
import lk.ghanim.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(
                dashboardService.getDashboardData()
        );
    }

    @GetMapping("/products")
    public ResponseEntity<List<AdminProductResponse>> getAdminProducts() {
        List<AdminProductResponse> products =
                productRepository.findByActiveTrue()
                        .stream()
                        .map(p -> {
                            BigDecimal cost = p.getCostPrice() != null
                                    ? p.getCostPrice()
                                    : BigDecimal.ZERO;

                            BigDecimal marginRetail = cost.compareTo(BigDecimal.ZERO) > 0
                                    ? p.getRetailPrice().subtract(cost)
                                    : null;

                            BigDecimal marginWholesale = cost.compareTo(BigDecimal.ZERO) > 0
                                    ? p.getWholesalePrice().subtract(cost)
                                    : null;

                            // Build imageUrls list
                            List<String> imageUrls = new ArrayList<>();
                            try {
                                if (p.getImages() != null && !p.getImages().isEmpty()) {
                                    imageUrls = p.getImages()
                                            .stream()
                                            .filter(img -> img != null && img.getImageUrl() != null)
                                            .sorted(Comparator.comparingInt(
                                                    img -> img.getSortOrder() != null
                                                            ? img.getSortOrder() : 999
                                            ))
                                            .map(ProductImage::getImageUrl)
                                            .collect(Collectors.toList());
                                }
                            } catch (Exception e) {
                                log.warn("Could not load images for product {}: {}",
                                        p.getId(), e.getMessage());
                            }

                            if (imageUrls.isEmpty() && p.getImageUrl() != null) {
                                imageUrls = List.of(p.getImageUrl());
                            }
                            String primaryImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

                            return AdminProductResponse.builder()
                                    .id(p.getId())
                                    .name(p.getName())
                                    .description(p.getDescription())
                                    .specifications(p.getSpecifications())
                                    .retailPrice(p.getRetailPrice())
                                    .wholesalePrice(p.getWholesalePrice())
                                    .costPrice(p.getCostPrice())
                                    .profitMarginRetail(marginRetail)
                                    .profitMarginWholesale(marginWholesale)
                                    .stock(p.getStock())
                                    .emoji(p.getEmoji())
                                    .imageUrl(primaryImageUrl)       // updated primary
                                    .imageUrls(imageUrls)            // new field
                                    .badge(p.getBadge() != null ? p.getBadge().name() : null)
                                    .categoryName(p.getCategory().getName())
                                    .categorySlug(p.getCategory().getSlug())
                                    .inStock(p.getStock() > 0)
                                    .active(p.isActive())
                                    .build();
                        })
                        .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        List<UserResponse> customers = userRepository.findAll()
                .stream()
                .filter(u ->
                        u.getRole() == User.Role.RETAIL || u.getRole() == User.Role.WHOLESALE)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole().name())
                        .businessName(u.getBusinessName())
                        .active(u.isActive())
                        .build()
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);

    }



}
