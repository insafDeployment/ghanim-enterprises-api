package lk.ghanim.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Retail price is required")
    @Positive(message = "Retail price must be positive")
    private BigDecimal retailPrice;

    @NotNull(message = "Wholesale price is required")
    @Positive(message = "Wholesale price must be positive")
    private BigDecimal wholesalePrice;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    private String emoji;

    private String imageUrl;

    private String badge;

    @NotNull(message = "Category is required")
    private Long categoryId;
}