package lk.ghanim.api.service;

import jakarta.transaction.Transactional;
import lk.ghanim.api.dto.request.OrderRequest;
import lk.ghanim.api.dto.response.OrderResponse;
import lk.ghanim.api.entity.Order;
import lk.ghanim.api.entity.OrderItem;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.exception.ResourceNotFoundException;
import lk.ghanim.api.repository.OrderRepository;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final BigDecimal FREE_DELIVERY_THRESHOLD =
            new BigDecimal("5000");
    private static final BigDecimal DELIVERY_FEE =
            new BigDecimal("350");

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        boolean isWholesale = user.getRole() == User.Role.WHOLESALE || user.getRole() == User.Role.ADMIN;

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for(OrderRequest.OrderItemRequest itemReq: request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for : " + product.getStock());
            }

            BigDecimal unitPrice = isWholesale ?
                    product.getWholesalePrice()
                    : product.getRetailPrice();

            BigDecimal totalPrice = unitPrice
                    .multiply(new BigDecimal(itemReq.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build();

            items.add(item);
            subtotal = subtotal.add(totalPrice);

            product.setStock(product.getStock() - itemReq.getQuantity());

            productRepository.save(product);
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (request.getPromoCode() != null) {
            discount = calculateDiscount(
                    request.getPromoCode(), subtotal
            );
        }

        BigDecimal deliveryFee = subtotal
                .subtract(discount)
                .compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ?
                BigDecimal.ZERO : DELIVERY_FEE;

        BigDecimal total = subtotal
                .subtract(discount)
                .add(deliveryFee);

        Order order = Order.builder()
                .user(user)
                .subtotal(subtotal)
                .discount(discount)
                .deliveryFee(deliveryFee)
                .total(total)
                .promoCode(request.getPromoCode())
                .status(Order.Status.PENDING)
                .priceType(isWholesale
                        ? Order.PriceType.WHOLESALE
                        : Order.PriceType.RETAIL)
                .deliveryAddress(request.getDeliveryAddress())
                .notes(request.getNotes())
                .build();

        order.setItems(items);
        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> getMyOrders() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow( () ->
                        new ResourceNotFoundException("Order not found " + id));

        return toResponse(order);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found: " + id));
        order.setStatus(Order.Status.valueOf(status));
        order.setUpdatedAt(java.time.LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    private BigDecimal calculateDiscount(String promoCode , BigDecimal subtotal) {
        return switch (promoCode.toUpperCase()) {
            case "SAVE10" -> subtotal
                    .multiply(new BigDecimal("0.10"))
                    .setScale(2, RoundingMode.HALF_UP);
            case "FLAT500" -> new BigDecimal("500");
            default -> BigDecimal.ZERO;
        };
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses =
                order.getItems().stream()
                        .map(item -> OrderResponse.OrderItemResponse.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .productEmoji(item.getProduct().getEmoji())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .totalPrice(item.getTotalPrice())
                                .build())
                        .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(
                        order.getUser().getFirstName() + " " +
                                order.getUser().getLastName()
                )
                .customerEmail(order.getUser().getEmail())
                .items(itemResponses)
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .deliveryFee(order.getDeliveryFee())
                .total(order.getTotal())
                .promoCode(order.getPromoCode())
                .status(order.getStatus().name())
                .priceType(order.getPriceType().name())
                .deliveryAddress(order.getDeliveryAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .build();
    }


}
