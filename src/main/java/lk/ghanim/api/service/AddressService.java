package lk.ghanim.api.service;


import jakarta.transaction.Transactional;
import lk.ghanim.api.dto.request.AddressRequest;
import lk.ghanim.api.dto.response.AddressResponse;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.entity.UserAddress;
import lk.ghanim.api.exception.ResourceNotFoundException;
import lk.ghanim.api.repository.UserAddressrepository;
import lk.ghanim.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserAddressrepository addressRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<AddressResponse> getAddresses() {
        User user = getCurrentUser();
        return addressRepository
                .findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<AddressResponse> getDefaultAddress(){
        User user = getCurrentUser();
        return addressRepository
                .findByUserAndIsDefaultTrue(user)
                .map(this::toResponse);
    }

    @Transactional
    public AddressResponse saveAddress(AddressRequest request) {
        User user = getCurrentUser();

        boolean isFirstAddress = addressRepository.countByUser(user) == 0;
        boolean shouldBeDefault = isFirstAddress || request.isSetAsDefault();

        if (shouldBeDefault) {
            addressRepository.clearDefaultForUser(user);
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .streetAddress(request.getStreetAddress())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .district(request.getDistrict())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .label(request.getLabel() != null
                        ? request.getLabel()
                        : UserAddress.Label.HOME)
                .isDefault(shouldBeDefault)
                .build();

        return toResponse(addressRepository.save(address));

    }

    @Transactional
    public AddressResponse setDefault(Long addressId) {
        User user = getCurrentUser();
        UserAddress address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.clearDefaultForUser(user);
        address.setDefault(true);
        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        User user = getCurrentUser();
        UserAddress address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.delete(address);

        // If deleted address was default, promote the most recent one
        if (address.isDefault()) {
            addressRepository
                    .findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                    .stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        addressRepository.save(next);
                    });
        }
    }



    private AddressResponse toResponse(UserAddress a){
        return AddressResponse.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .streetAddress(a.getStreetAddress())
                .landmark(a.getLandmark())
                .city(a.getCity())
                .district(a.getDistrict())
                .province(a.getProvince())
                .postalCode(a.getPostalCode())
                .label(a.getLabel())
                .isDefault(a.isDefault())
                .createdAt(a.getCreatedAt())
                .formattedAddress(buildFormattedAddress(a))
                .formattedContact(buildFormattedContact(a))
                .build();

    }

    private String buildFormattedAddress(UserAddress a) {
        StringBuilder sb = new StringBuilder();
        sb.append(a.getStreetAddress());
        if (a.getLandmark() != null && !a.getLandmark().isBlank())
            sb.append(", ").append(a.getLandmark());
        sb.append(", ").append(a.getCity());
        sb.append(", ").append(a.getDistrict());
        sb.append(", ").append(a.getProvince());
        if (a.getPostalCode() != null && !a.getPostalCode().isBlank())
            sb.append(", Postal: ").append(a.getPostalCode());
        return sb.toString();
    }

    private String buildFormattedContact(UserAddress a) {
        String phone = a.getPhone();

        if (phone.startsWith("94")) {
            phone = "0" + phone.substring(2);
        } else if (!phone.startsWith("0")) {
            phone = "0" + phone;
        }

        return a.getFullName()
                + " | " + phone
                + " — [" + a.getLabel().name() + "]";
    }
}
