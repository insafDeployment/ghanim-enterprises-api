package lk.ghanim.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.ghanim.api.entity.UserAddress;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0[0-9]{9}|[0-9]{9}|94[0-9]{9})$", message = "Enter a valid 9-digit Sri Lankan number")
    private String phone;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String landmark;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Province is required")
    private String province;

    @Size(max = 5, message = "Postal code must be 5 digits")
    private String postalCode;

    private UserAddress.Label label = UserAddress.Label.HOME;

    private boolean setAsDefault = true;
}
