package lk.ghanim.api.config;

import lk.ghanim.api.entity.Category;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.CategoryRepository;
import lk.ghanim.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initCategories();
        initAdminUser();
    }

    private void initCategories() {
        if (categoryRepository.count() > 0) return;

        List<Category> categories = List.of(
                Category.builder()
                        .name("Kitchenware").slug("kitchenware")
                        .emoji("🍳")
                        .description("Premium cooking essentials")
                        .active(true).build(),
                Category.builder()
                        .name("Aluminium").slug("aluminium")
                        .emoji("🪣")
                        .description("Durable aluminium products")
                        .active(true).build(),
                Category.builder()
                        .name("Plastic").slug("plastic")
                        .emoji("🧴")
                        .description("Everyday plastic solutions")
                        .active(true).build(),
                Category.builder()
                        .name("Gift Items").slug("gift-items")
                        .emoji("🎁")
                        .description("Perfect for every occasion")
                        .active(true).build(),
                Category.builder()
                        .name("Umbrellas").slug("umbrellas")
                        .emoji("☂️")
                        .description("Stay dry in style")
                        .active(true).build(),
                Category.builder()
                        .name("Lighting").slug("lighting")
                        .emoji("💡")
                        .description("Brighten your space")
                        .active(true).build(),
                Category.builder()
                        .name("General").slug("general")
                        .emoji("🛒")
                        .description("Everything else you need")
                        .active(true).build()
        );

        categoryRepository.saveAll(categories);
        System.out.println("✅ Categories initialized");
    }

    private void initAdminUser() {
        if (userRepository.existsByEmail("admin@ghanim.lk")) return;

        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@ghanim.lk")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .active(true)
                .build();

        userRepository.save(admin);

        User wholesale = User.builder()
                .firstName("Wholesale")
                .lastName("Customer")
                .email("wholesale@ghanim.lk")
                .password(passwordEncoder.encode("wholesale123"))
                .role(User.Role.WHOLESALE)
                .businessName("Test Wholesale Business")
                .active(true)
                .build();

        userRepository.save(wholesale);

        System.out.println("✅ Admin and wholesale users initialized");
    }
}