package lk.ghanim.api.dto.response;

import lk.ghanim.api.entity.UserAddress;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String streetAddress;
    private String landmark;
    private String city;
    private String district;
    private String province;
    private String postalCode;
    private UserAddress.Label label;
    private boolean isDefault;
    private LocalDateTime createdAt;

    private String formattedAddress;

    private String formattedContact;
}
