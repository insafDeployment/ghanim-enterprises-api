package lk.ghanim.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotEmpty(message = "Order must have atleast one item")
    private List<OrderItemRequest> items;

    private String promoCode;
    private String deliveryAddress;
    private String notes;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}
