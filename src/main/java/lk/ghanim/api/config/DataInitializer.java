package lk.ghanim.api.config;

import lk.ghanim.api.entity.Category;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.CategoryRepository;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.UserRepository;
import lk.ghanim.api.service.SmartSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final SmartSearchService smartSearchService;

    @Override
    public void run(String... args) {
        initCategories();
        initAdminUser();
        initProducts();
        generateEmbeddings();
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

    private void initProducts() {
        if (productRepository.count() > 0) return;

        Category kitchenware = categoryRepository
                .findBySlug("kitchenware").orElseThrow();
        Category aluminium = categoryRepository
                .findBySlug("aluminium").orElseThrow();
        Category plastic = categoryRepository
                .findBySlug("plastic").orElseThrow();
        Category giftItems = categoryRepository
                .findBySlug("gift-items").orElseThrow();
        Category umbrellas = categoryRepository
                .findBySlug("umbrellas").orElseThrow();
        Category lighting = categoryRepository
                .findBySlug("lighting").orElseThrow();
        Category general = categoryRepository
                .findBySlug("general").orElseThrow();

        List<Product> products = List.of(
                Product.builder()
                        .name("Non-Stick Frying Pan")
                        .description("Premium non-stick frying pan perfect for everyday cooking. Durable coating ensures food does not stick and easy cleanup after cooking.")
                        .retailPrice(new BigDecimal("1850"))
                        .wholesalePrice(new BigDecimal("1400"))
                        .stock(45).emoji("🍳")
                        .badge(Product.Badge.BEST_SELLER)
                        .category(kitchenware).active(true).build(),
                Product.builder()
                        .name("Kitchen Knife Set")
                        .description("Professional kitchen knife set including chef knife bread knife and paring knife. Made from high carbon stainless steel for lasting sharpness.")
                        .retailPrice(new BigDecimal("2800"))
                        .wholesalePrice(new BigDecimal("2100"))
                        .stock(32).emoji("🔪")
                        .badge(Product.Badge.BEST_SELLER)
                        .category(kitchenware).active(true).build(),

                Product.builder()
                        .name("Cutting Board")
                        .description("Large bamboo cutting board with juice groove around the edges. Naturally antimicrobial and gentle on knife blades.")
                        .retailPrice(new BigDecimal("950"))
                        .wholesalePrice(new BigDecimal("720"))
                        .stock(60).emoji("🪵")
                        .category(kitchenware).active(true).build(),

                Product.builder()
                        .name("Bowl Set")
                        .description("Set of 6 stainless steel mixing bowls in graduated sizes. Ideal for meal prep baking and serving. Stackable design saves cabinet space.")
                        .retailPrice(new BigDecimal("1200"))
                        .wholesalePrice(new BigDecimal("900"))
                        .stock(40).emoji("🥣")
                        .badge(Product.Badge.NEW)
                        .category(kitchenware).active(true).build(),

                Product.builder()
                        .name("Cooking Pot Set")
                        .description("Set of 3 stainless steel cooking pots with glass lids. Suitable for all stovetops including induction. Dishwasher safe.")
                        .retailPrice(new BigDecimal("3500"))
                        .wholesalePrice(new BigDecimal("2650"))
                        .stock(25).emoji("🫕")
                        .category(kitchenware).active(true).build(),

                // Aluminium
                Product.builder()
                        .name("Aluminium Storage Bin")
                        .description("Heavy duty aluminium storage bin suitable for home and industrial use. Rust resistant and easy to clean with secure lid included.")
                        .retailPrice(new BigDecimal("2400"))
                        .wholesalePrice(new BigDecimal("1800"))
                        .stock(28).emoji("🪣")
                        .badge(Product.Badge.NEW)
                        .category(aluminium).active(true).build(),

                Product.builder()
                        .name("Aluminium Ladder")
                        .description("Heavy duty aluminium step ladder with non slip rubber feet. Lightweight yet strong enough to hold up to 150kg safely. 6 steps.")
                        .retailPrice(new BigDecimal("8500"))
                        .wholesalePrice(new BigDecimal("6500"))
                        .stock(12).emoji("🪜")
                        .category(aluminium).active(true).build(),

                Product.builder()
                        .name("Water Bucket")
                        .description("Standard aluminium water bucket for household and industrial use. Durable lightweight and rust resistant with steel wire handle.")
                        .retailPrice(new BigDecimal("750"))
                        .wholesalePrice(new BigDecimal("560"))
                        .stock(80).emoji("🧺")
                        .badge(Product.Badge.SALE)
                        .category(aluminium).active(true).build(),

                Product.builder()
                        .name("Aluminium Storage Rack")
                        .description("Multi tier aluminium storage rack for garage kitchen or warehouse. Easy to assemble with no tools required. 4 shelves 50kg per shelf.")
                        .retailPrice(new BigDecimal("3200"))
                        .wholesalePrice(new BigDecimal("2400"))
                        .stock(0).emoji("🗄️")
                        .category(aluminium).active(true).build(),

                // Plastic
                Product.builder()
                        .name("Plastic Storage Box")
                        .description("Stackable plastic storage box with secure clip on lid. Perfect for organising clothes toys documents and household items.")
                        .retailPrice(new BigDecimal("650"))
                        .wholesalePrice(new BigDecimal("480"))
                        .stock(110).emoji("🧴")
                        .category(plastic).active(true).build(),

                Product.builder()
                        .name("Plastic Containers Set")
                        .description("Set of 8 airtight plastic food containers in various sizes. BPA free and microwave safe for safe food storage and meal prep.")
                        .retailPrice(new BigDecimal("1100"))
                        .wholesalePrice(new BigDecimal("820"))
                        .stock(76).emoji("🫙")
                        .badge(Product.Badge.SALE)
                        .category(plastic).active(true).build(),

                Product.builder()
                        .name("Plastic Laundry Basket")
                        .description("Large capacity plastic laundry basket with ventilation holes. Lightweight and durable with comfortable carry handles.")
                        .retailPrice(new BigDecimal("880"))
                        .wholesalePrice(new BigDecimal("660"))
                        .stock(55).emoji("🧺")
                        .category(plastic).active(true).build(),

                // Gift Items
                Product.builder()
                        .name("Gift Hamper Set")
                        .description("Beautifully curated gift hamper perfect for birthdays weddings and celebrations. Includes premium items presented in an elegant wicker basket with ribbon.")
                        .retailPrice(new BigDecimal("3500"))
                        .wholesalePrice(new BigDecimal("2700"))
                        .stock(18).emoji("🎁")
                        .badge(Product.Badge.SALE)
                        .category(giftItems).active(true).build(),

                Product.builder()
                        .name("Candle Gift Set")
                        .description("Elegant scented candle gift set with three candles in Rose Jasmine and Sandalwood fragrances. Natural soy wax with up to 40 hours burn time each.")
                        .retailPrice(new BigDecimal("1800"))
                        .wholesalePrice(new BigDecimal("1350"))
                        .stock(35).emoji("🕯️")
                        .badge(Product.Badge.SALE)
                        .category(giftItems).active(true).build(),

                Product.builder()
                        .name("Photo Frame Set")
                        .description("Set of 3 decorative photo frames in matching design. Perfect for displaying family memories on walls or tabletops. Portrait and landscape options.")
                        .retailPrice(new BigDecimal("1200"))
                        .wholesalePrice(new BigDecimal("900"))
                        .stock(54).emoji("🖼️")
                        .category(giftItems).active(true).build(),

                // Umbrellas
                Product.builder()
                        .name("Folding Umbrella")
                        .description("Compact folding umbrella with automatic open and close button. Windproof frame and UV protection coating UPF 50 plus. 100cm diameter when open.")
                        .retailPrice(new BigDecimal("950"))
                        .wholesalePrice(new BigDecimal("720"))
                        .stock(65).emoji("☂️")
                        .badge(Product.Badge.SALE)
                        .category(umbrellas).active(true).build(),

                Product.builder()
                        .name("Golf Umbrella")
                        .description("Extra large golf umbrella providing maximum coverage. Double canopy design allows wind to pass through preventing inversion. 150cm diameter.")
                        .retailPrice(new BigDecimal("2200"))
                        .wholesalePrice(new BigDecimal("1650"))
                        .stock(25).emoji("⛱️")
                        .badge(Product.Badge.NEW)
                        .category(umbrellas).active(true).build(),

                // Lighting
                Product.builder()
                        .name("LED Ceiling Light")
                        .description("Modern LED ceiling light with warm white glow. Energy efficient and long lasting with up to 25000 hours lifespan. 18W replaces 100W bulb.")
                        .retailPrice(new BigDecimal("1200"))
                        .wholesalePrice(new BigDecimal("900"))
                        .stock(45).emoji("💡")
                        .category(lighting).active(true).build(),

                Product.builder()
                        .name("LED Bulb Pack")
                        .description("Pack of 6 LED bulbs replacing 60W incandescent bulbs using only 9W. Instant full brightness with no warm up time. E27 screw base.")
                        .retailPrice(new BigDecimal("850"))
                        .wholesalePrice(new BigDecimal("640"))
                        .stock(120).emoji("🔆")
                        .category(lighting).active(true).build(),

                Product.builder()
                        .name("Table Lamp")
                        .description("Elegant table lamp with adjustable brightness touch dimmer. Perfect for bedside desk or living room. Modern design with fabric shade. E14 bulb included.")
                        .retailPrice(new BigDecimal("1800"))
                        .wholesalePrice(new BigDecimal("1350"))
                        .stock(30).emoji("🪔")
                        .badge(Product.Badge.SALE)
                        .category(lighting).active(true).build(),

                // General
                Product.builder()
                        .name("Storage Basket")
                        .description("Woven storage basket for organising towels magazines toys and more. Natural seagrass material that looks great in any room. Carry handles included.")
                        .retailPrice(new BigDecimal("880"))
                        .wholesalePrice(new BigDecimal("660"))
                        .stock(38).emoji("🧺")
                        .category(general).active(true).build(),

                Product.builder()
                        .name("Cleaning Set")
                        .description("Complete household cleaning set including flat microfibre mop broom dustpan and scrubbing brush. Extendable aluminium handles suitable for all floor types.")
                        .retailPrice(new BigDecimal("1450"))
                        .wholesalePrice(new BigDecimal("1090"))
                        .stock(42).emoji("🧹")
                        .badge(Product.Badge.SALE)
                        .category(general).active(true).build()
        );

        productRepository.saveAll(products);
        System.out.println("✅ " + products.size() + " products initialized");
    }

    private void generateEmbeddings() {
        long productsWithoutEmbedding = productRepository
                .findByActiveTrue()
                .stream()
                .filter(p -> p.getEmbedding() == null
                        || p.getEmbedding().isBlank())  // ✅ add isBlank check
                .count();

        if (productsWithoutEmbedding == 0) {
            System.out.println("✅ All products already have embeddings");
            return;
        }

        System.out.println("⏳ Generating embeddings for " +
                productsWithoutEmbedding + " products...");
        smartSearchService.generateEmbeddingsForAllProducts();
        System.out.println("✅ Embeddings ready");
    }
}