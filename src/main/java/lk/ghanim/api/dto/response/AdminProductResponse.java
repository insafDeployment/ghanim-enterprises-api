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
public class AdminProductResponse {
    private Long id;
    private String name;
    private String description;
    private String specifications;
    private BigDecimal retailPrice;
    private BigDecimal wholesalePrice;
    private BigDecimal costPrice;
    private BigDecimal profitMarginRetail;
    private BigDecimal profitMarginWholesale;
    private Integer stock;
    private String emoji;
    private String imageUrl;
    private List<String> imageUrls;
    private String badge;
    private String categoryName;
    private String categorySlug;
    private boolean inStock;
    private boolean active;
}
