package lk.ghanim.api.controller;


import lk.ghanim.api.dto.response.DashboardResponse;
import lk.ghanim.api.dto.response.OrderResponse;
import lk.ghanim.api.dto.response.UserResponse;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.UserRepository;
import lk.ghanim.api.service.DashboardService;
import lk.ghanim.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(
                dashboardService.getDashboardData()
        );
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
