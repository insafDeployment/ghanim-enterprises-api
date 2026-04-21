package lk.ghanim.api.service;


import lk.ghanim.api.dto.response.DashboardResponse;
import lk.ghanim.api.entity.Order;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.CategoryRepository;
import lk.ghanim.api.repository.OrderRepository;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public DashboardResponse getDashboardData() {

        long totalProducts = productRepository
                .findByActiveTrue().size();
        long totalOrders = orderRepository.count();
        long totalCustomers = userRepository
                .findAll().stream()
                .filter(u -> u.getRole() == User.Role.RETAIL || u.getRole() == User.Role.WHOLESALE)
                .count();

        BigDecimal totalRevenue = orderRepository.findAll()
                .stream()
                .filter( o -> o.getStatus() == Order.Status.DELIVERED)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Order> allOrders = orderRepository.findAll();
        long pendingOrders = allOrders.stream()
                .filter(o->o.getStatus() == Order.Status.PENDING)
                .count();
        long processingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.PROCESSING)
                .count();
        long deliveredOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.DELIVERED)
                .count();
        long cancelledOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.CANCELLED)
                .count();

        // Recent orders — last 10
        List<DashboardResponse.RecentOrderSummary> recentOrders =
                allOrders.stream()
                        .sorted((a, b) -> b.getCreatedAt()
                                .compareTo(a.getCreatedAt()))
                        .limit(10)
                        .map(order ->
                                DashboardResponse.RecentOrderSummary.builder()
                                        .id(order.getId())
                                        .customerName(
                                                order.getUser().getFirstName() + " " +
                                                        order.getUser().getLastName()
                                        )
                                        .customerEmail(order.getUser().getEmail())
                                        .itemCount(order.getItems().size())
                                        .total(order.getTotal())
                                        .status(order.getStatus().name())
                                        .priceType(order.getPriceType().name())
                                        .createdAt(order.getCreatedAt().toString())
                                        .build()
                        )
                        .collect(Collectors.toList());

        // Top products by quantity sold
        Map<Long, Integer> productSalesMap = allOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.summingInt(item -> item.getQuantity())
                ));

        Map<Long, BigDecimal> productRevenueMap = allOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getId(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> item.getTotalPrice(),
                                BigDecimal::add
                        )
                ));

        List<DashboardResponse.TopProductSummary> topProducts =
                productSalesMap.entrySet().stream()
                        .sorted((a, b) ->
                                b.getValue().compareTo(a.getValue())
                        )
                        .limit(5)
                        .map(entry -> {
                            var product = productRepository
                                    .findById(entry.getKey())
                                    .orElseThrow();
                            return DashboardResponse.TopProductSummary
                                    .builder()
                                    .productId(product.getId())
                                    .productName(product.getName())
                                    .productEmoji(product.getEmoji())
                                    .categoryName(product.getCategory().getName())
                                    .totalSold(entry.getValue())
                                    .totalRevenue(productRevenueMap
                                            .getOrDefault(
                                                    entry.getKey(),
                                                    BigDecimal.ZERO
                                            )
                                    )
                                    .build();
                        })
                        .collect(Collectors.toList());

        // Category summary
        List<DashboardResponse.CategorySummary> categorySummary =
                categoryRepository.findByActiveTrue().stream()
                        .map(cat ->
                                DashboardResponse.CategorySummary.builder()
                                        .categoryName(cat.getName())
                                        .categorySlug(cat.getSlug())
                                        .productCount(
                                                (long) productRepository
                                                        .findByCategorySlugAndActiveTrue(
                                                                cat.getSlug()
                                                        ).size()
                                        )
                                        .build()
                        )
                        .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalRevenue(totalRevenue)
                .pendingOrders(pendingOrders)
                .processingOrders(processingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .recentOrders(recentOrders)
                .topProducts(topProducts)
                .categorySummary(categorySummary)
                .build();

    }
}
