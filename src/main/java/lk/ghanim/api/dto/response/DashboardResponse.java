package lk.ghanim.api.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalProducts;
    private Long totalOrders;
    private Long totalCustomers;
    private BigDecimal totalRevenue;
    private Long pendingOrders;
    private Long processingOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private List<RecentOrderSummary> recentOrders;
    private List<TopProductSummary> topProducts;
    private List<CategorySummary> categorySummary;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrderSummary {
        private Long id;
        private String customerName;
        private String customerEmail;
        private Integer itemCount;
        private BigDecimal total;
        private String status;
        private String priceType;
        private String createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductSummary {
        private Long productId;
        private String productName;
        private String productEmoji;
        private String categoryName;
        private Integer totalSold;
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySummary {
        private String categoryName;
        private String categorySlug;
        private Long productCount;
    }
}
