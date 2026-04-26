package lk.ghanim.api.config;

import lk.ghanim.api.entity.Category;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.entity.ProductImage;
import lk.ghanim.api.entity.User;
import lk.ghanim.api.repository.CategoryRepository;
import lk.ghanim.api.repository.ProductImageRepository;
import lk.ghanim.api.repository.ProductRepository;
import lk.ghanim.api.repository.UserRepository;
import lk.ghanim.api.service.SmartSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final ProductImageRepository productImageRepository;

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

        // ─── CATEGORIES ───────────────────────────────────────────────
        Category kitchenware = categoryRepository.findBySlug("kitchenware").orElseThrow();
        Category aluminium   = categoryRepository.findBySlug("aluminium").orElseThrow();
        Category plastic     = categoryRepository.findBySlug("plastic").orElseThrow();
        Category giftItems   = categoryRepository.findBySlug("gift-items").orElseThrow();
        Category lighting    = categoryRepository.findBySlug("lighting").orElseThrow();
        Category general     = categoryRepository.findBySlug("general").orElseThrow();
        Category umbrellas = categoryRepository.findBySlug("umbrellas").orElseThrow();


        // ══════════════════════════════════════════════════════════════
        // LIGHTING
        // ══════════════════════════════════════════════════════════════

        // 1. Natural Jute Rope Hanging Light
        Product juteLight = Product.builder()
                .name("Natural Jute Rope Hanging Light")
                .description("Handcrafted jute rope pendant light with a warm vintage look. Includes an ST64 LED filament bulb (4W, 2200K warm white). Standard E27 base, 220V compatible. 1 metre drop length — ideal for dining areas, cafés, and home interiors. Perfect solo or clustered in groups of 3, 5, or 7.")
                .specifications("""
                    Rope Material    : 100% natural jute fibre
                    Drop Length      : 1 metre (adjustable via ceiling canopy)
                    Bulb Holder      : E27 screw base — 220V AC compatible
                    Bulb Included    : ST64 LED filament, 4W, 2200K–2400K warm amber
                    Applications     : Dining pendants, café clusters, home bars, retail boutiques
                    Indoor Use       : Jute treated for indoor humidity tolerance
                    Warranty         : 12 months electrical, 6 months jute fibre
                    """)
                .retailPrice(new BigDecimal("1200"))
                .wholesalePrice(new BigDecimal("900"))
                .costPrice(new BigDecimal("650"))
                .stock(30).emoji("💡")
                .badge(Product.Badge.NEW)
                .category(lighting).active(true).build();
        juteLight = productRepository.save(juteLight);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(juteLight).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1776938862/3_rtefz1.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(juteLight).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777185352/5_lndyll.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(juteLight).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777185355/1_gapaly.jpg").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(juteLight).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777185357/2_usnsj4.jpg").isPrimary(false).sortOrder(4).build()
        ));

        // 2. 6W LED Wall Lamp
        Product wallLamp6w = Product.builder()
                .name("6W LED Wall Lamp (Up & Down Light)")
                .description("Modern aluminium wall lamp with dual up-and-down beam direction. Features advanced LED chips with condenser lenses for a focused, elegant glow. Ideal for living rooms, bedrooms, corridors, and outdoor walls. Sleek matte black finish suits both contemporary and minimalist interiors. Energy efficient at only 6W.")
                .specifications("""
                    Power            : 6W LED
                    Beam Direction   : Up & Down (dual)
                    Body Material    : Die-cast aluminium (Seiko grade)
                    Lens             : Condenser lens for focused beam
                    Finish           : Matte black
                    Voltage          : 220V AC
                    Applications     : Living room, bedroom, corridor, outdoor wall
                    IP Rating        : IP54 (splash resistant)
                    """)
                .retailPrice(new BigDecimal("2200"))
                .wholesalePrice(new BigDecimal("1650"))
                .costPrice(new BigDecimal("1200"))
                .stock(40).emoji("🔆")
                .badge(Product.Badge.NEW)
                .category(lighting).active(true).build();
        wallLamp6w = productRepository.save(wallLamp6w);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(wallLamp6w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777186817/5_uqbzwc.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(wallLamp6w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777186812/Real_6w_wallLamp_jtdgec.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(wallLamp6w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777186816/4_bil9im.jpg").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(wallLamp6w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777186815/6_bafz9u.jpg").isPrimary(false).sortOrder(4).build()
        ));

        // 3. LED Series Light 9m
        Product ledSeries = Product.builder()
                .name("LED Series Light 9m")
                .description("Vibrant 9-metre LED string lights available in 6 colours — Red, Blue, White, Warm White, Yellow, and Green. Perfect for home decorations, festivals, parties, and events. Plug-and-play design with 220V compatibility. Adds a festive and cheerful atmosphere to any indoor space.")
                .specifications("""
                    Length           : 9 metres
                    Available Colors : Red, Blue, White, Warm White, Yellow, Green
                    Voltage          : 220V AC
                    Use              : Indoor decorative lighting
                    Applications     : Festivals, parties, home décor, events
                    """)
                .retailPrice(new BigDecimal("650"))
                .wholesalePrice(new BigDecimal("480"))
                .costPrice(new BigDecimal("350"))
                .stock(100).emoji("🎇")
                .badge(Product.Badge.BEST_SELLER)
                .category(lighting).active(true).build();
        ledSeries = productRepository.save(ledSeries);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(ledSeries).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187321/real_ledSeries_l4y2g9.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(ledSeries).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187324/led4_vbb40p.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(ledSeries).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187326/led3_hpu6xa.png").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(ledSeries).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187328/led2_ldmh9w.jpg").isPrimary(false).sortOrder(4).build()
        ));

        // 4. Fibit 12W Rechargeable Bulb
        Product fibit12w = Product.builder()
                .name("Fibit 12W Rechargeable LED Bulb")
                .description("FIBIT 12W rechargeable LED bulb that works during power cuts. Built-in 1500mAh battery provides backup lighting automatically when electricity fails. B22 bayonet base, cool daylight output at 1000 lumens. AC/DC compatible — ideal for Sri Lankan homes and businesses.")
                .specifications("""
                    Brand            : FIBIT
                    Power            : 12W
                    Base             : B22 Bayonet
                    Battery          : 1500mAh built-in rechargeable
                    Lumens           : 1000lm
                    Color Temp       : Cool Daylight
                    Voltage          : AC/DC 85–265V
                    Backup           : Auto-on during power cut
                    """)
                .retailPrice(new BigDecimal("1750"))
                .wholesalePrice(new BigDecimal("1350"))
                .costPrice(new BigDecimal("1100"))
                .stock(60).emoji("💡")
                .badge(Product.Badge.NEW)
                .category(lighting).active(true).build();
        fibit12w = productRepository.save(fibit12w);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(fibit12w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188032/fibit1_ixvsvw.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(fibit12w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188032/fibit2_hjwqxr.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(fibit12w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188030/fibit3_wydftr.png").isPrimary(false).sortOrder(3).build()
        ));

        // 5. Fibit 30W Rechargeable Bulb
        Product fibit30w = Product.builder()
                .name("Fibit 30W Rechargeable LED Bulb")
                .description("High-power FIBIT 30W rechargeable LED bulb with automatic power-cut backup. Delivers bright, reliable lighting for larger rooms and commercial spaces. B22 bayonet base with AC/DC compatibility. Never be left in the dark during load shedding again.")
                .specifications("""
                    Brand            : FIBIT
                    Power            : 30W
                    Base             : B22 Bayonet
                    Color Temp       : Cool Daylight
                    Voltage          : AC/DC 85–265V
                    Backup           : Auto-on during power cut
                    Best For         : Large rooms, shops, offices
                    """)
                .retailPrice(new BigDecimal("2200"))
                .wholesalePrice(new BigDecimal("1750"))
                .costPrice(new BigDecimal("1500"))
                .stock(40).emoji("💡")
                .category(lighting).active(true).build();
        fibit30w = productRepository.save(fibit30w);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(fibit30w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188032/fibit1_ixvsvw.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(fibit30w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188032/fibit2_hjwqxr.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(fibit30w).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188030/fibit3_wydftr.png").isPrimary(false).sortOrder(3).build()
        ));


        // ══════════════════════════════════════════════════════════════
        // GENERAL
        // ══════════════════════════════════════════════════════════════

        // 6. 3-in-1 Fast Charging Cable
        Product cable3in1 = Product.builder()
                .name("3-in-1 Fast Charging Cable (1m)")
                .description("Heavy-duty silicone charging cable with three connectors in one — Type-C, Micro USB, and Lightning. Supports fast charging up to 66W. Durable zinc alloy tips with tangle-free flexible silicone jacket. Compatible with Android and iOS devices. A must-have for home, office, or travel use.")
                .specifications("""
                    Cable Length     : 1 metre
                    Connectors       : Type-C, Micro USB, Lightning (3-in-1)
                    Max Charging     : 66W fast charge
                    Cable Material   : Premium silicone — tangle-free, heat resistant
                    Connector Tips   : Zinc alloy metal housing
                    Compatibility    : Android, iOS, and most USB-A devices
                    Input            : USB-A
                    """)
                .retailPrice(new BigDecimal("1500"))
                .wholesalePrice(new BigDecimal("1100"))
                .costPrice(new BigDecimal("800"))
                .stock(60).emoji("🔌")
                .badge(Product.Badge.NEW)
                .category(general).active(true).build();
        cable3in1 = productRepository.save(cable3in1);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(cable3in1).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777134934/c4_ou81d3.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(cable3in1).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777134932/c5_cvmxzt.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(cable3in1).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777134936/c3_rbx44c.png").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(cable3in1).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777134930/RealFastCharging_gm6jag.png").isPrimary(false).sortOrder(4).build()
        ));

        // 7. A6S Earbud
        Product a6sEarbud = Product.builder()
                .name("A6S TWS Wireless Earbuds")
                .description("Compact true wireless earbuds with instant Bluetooth pairing — simply open the case and connect. Clear sound with punchy bass, suitable for music, calls, and daily use. Comes with a charging case for extended battery life. Lightweight and comfortable for all-day wear.")
                .specifications("""
                    Model            : A6S TWS
                    Connectivity     : Bluetooth 5.0
                    Pairing          : Instant auto-pair on case open
                    Battery          : Earbuds + charging case included
                    Use              : Music, calls, daily commute
                    Compatibility    : Android and iOS
                    """)
                .retailPrice(new BigDecimal("1800"))
                .wholesalePrice(new BigDecimal("1350"))
                .costPrice(new BigDecimal("950"))
                .stock(45).emoji("🎧")
                .badge(Product.Badge.BEST_SELLER)
                .category(general).active(true).build();
        a6sEarbud = productRepository.save(a6sEarbud);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(a6sEarbud).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187061/1_ajynng.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(a6sEarbud).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187059/Real_a6s_sux6xg.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(a6sEarbud).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187057/8_qxuvs5.png").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(a6sEarbud).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187056/7_kp5kjp.png").isPrimary(false).sortOrder(4).build()
        ));

        // 8. Dummy Camera Solar Light
        Product dummyCamera = Product.builder()
                .name("Solar Dummy Camera Security Light")
                .description("Solar-powered LED security light designed to look like a CCTV camera — an effective visual deterrent for intruders. Features a PIR motion sensor and remote control for easy operation. Weatherproof design suitable for outdoor walls, gates, and entrances. No wiring needed.")
                .specifications("""
                    Type             : Solar-powered LED flood light (dummy camera style)
                    Sensor           : PIR motion sensor
                    Control          : Remote control included
                    Power            : Solar panel (no wiring required)
                    Installation     : Wall mount — outdoor use
                    Weatherproof     : Yes
                    Applications     : Gates, driveways, gardens, building entrances
                    """)
                .retailPrice(new BigDecimal("1500"))
                .wholesalePrice(new BigDecimal("1200"))
                .costPrice(new BigDecimal("950"))
                .stock(35).emoji("📷")
                .badge(Product.Badge.NEW)
                .category(general).active(true).build();
        dummyCamera = productRepository.save(dummyCamera);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(dummyCamera).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187177/dummy_06_mwmmzq.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(dummyCamera).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187175/dummy02_nzjb9j.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(dummyCamera).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187172/dummy_04_jixbef.png").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(dummyCamera).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187169/dummy_03_po0dej.png").isPrimary(false).sortOrder(4).build()
        ));

        // 9. Personal Body Weight Scale
        Product personalScale = Product.builder()
                .name("Digital Personal Body Weight Scale")
                .description("Elegant round tempered glass bathroom scale with a clear LCD display. Accurate weight readings up to 180kg with 0.1kg precision. Step-on technology — no buttons needed, just stand and read. Slim and stylish design fits any bathroom. Batteries included.")
                .specifications("""
                    Platform         : Round tempered glass
                    Display          : LCD digital readout
                    Capacity         : 180kg maximum
                    Precision        : 0.1kg
                    Technology       : Step-on auto activation
                    Unit             : kg / lb switchable
                    Power            : Battery powered (included)
                    """)
                .retailPrice(new BigDecimal("2750"))
                .wholesalePrice(new BigDecimal("2200"))
                .costPrice(new BigDecimal("2000"))
                .stock(25).emoji("⚖️")
                .category(general).active(true).build();
        personalScale = productRepository.save(personalScale);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(personalScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187415/personalScale_02_lqxnay.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(personalScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187417/personalScale_03_bt5t1v.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(personalScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187412/real_personalScale_c4gvag.png").isPrimary(false).sortOrder(3).build()
        ));

        // 10. Fulinmen Wall Switch Socket
        Product wallSocket = Product.builder()
                .name("Fulinmen Wall Switch Socket (13A Multi Plug)")
                .description("Premium FULINMEN 1-gang 3-pin universal wall socket with integrated switch and indicator light. Accepts plugs from multiple countries — ideal for Sri Lankan homes and offices. Rated 13A with a lifespan of 50,000 switch operations. Slim 86x86mm panel fits standard wall boxes.")
                .specifications("""
                    Brand            : FULINMEN
                    Rating           : 13A, 110V–250V AC
                    Type             : 1 Gang 3-Pin Universal Socket with Switch
                    Indicator        : Red neon indicator light
                    Dimensions       : 86mm x 86mm x 14mm
                    Lifespan         : 50,000 switch operations
                    Compatibility    : Standard wall box installation
                    """)
                .retailPrice(new BigDecimal("750"))
                .wholesalePrice(new BigDecimal("550"))
                .costPrice(new BigDecimal("380"))
                .stock(80).emoji("🔌")
                .category(general).active(true).build();
        wallSocket = productRepository.save(wallSocket);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(wallSocket).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187580/real_plugBase_svkadx.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(wallSocket).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187582/f3_gls6eq.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(wallSocket).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187585/f2_s8cfmz.png").isPrimary(false).sortOrder(3).build()
        ));

        // 11. Cloth Drying Rack
        Product clothRack = Product.builder()
                .name("Foldable Cloth Drying Rack")
                .description("Sturdy multi-tier clothes drying rack made from coated steel — rust-proof and built to last. Spacious design holds a full laundry load with ease. Folds flat for compact storage when not in use. No assembly required, ready straight out of the box. Perfect for indoor use in any room.")
                .specifications("""
                    Material         : Coated steel frame — rust-proof
                    Tiers            : Multi-tier with wing extensions
                    Foldable         : Yes — flat fold for easy storage
                    Assembly         : No assembly required
                    Use              : Indoor laundry drying
                    Includes         : Peg basket holder
                    """)
                .retailPrice(new BigDecimal("3250"))
                .wholesalePrice(new BigDecimal("2750"))
                .costPrice(new BigDecimal("2500"))
                .stock(20).emoji("👕")
                .category(general).active(true).build();
        clothRack = productRepository.save(clothRack);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(clothRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188528/3_eavbrk.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(clothRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188531/2_aajefz.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(clothRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188529/1_njw98t.jpg").isPrimary(false).sortOrder(3).build()
        ));

        // 12. T-Wolf TF200 Gaming Keyboard & Mouse
        Product gamingKeyboard = Product.builder()
                .name("T-Wolf TF200 RGB Gaming Keyboard & Mouse Combo")
                .description("Mechanical-feel RGB gaming keyboard with full rainbow backlight and an included RGB gaming mouse. Ergonomic key design for comfortable long sessions. USB plug-and-play with no driver required. Great for gaming, typing, and everyday PC use. Solid build quality at an unbeatable price.")
                .specifications("""
                    Model            : T-Wolf TF200
                    Type             : Mechanical-feel membrane keyboard + mouse combo
                    Backlight        : RGB rainbow LED
                    Connection       : USB plug-and-play (no driver required)
                    Mouse            : RGB gaming mouse included
                    Compatibility    : Windows / Mac
                    Layout           : Full size
                    """)
                .retailPrice(new BigDecimal("2750"))
                .wholesalePrice(new BigDecimal("2000"))
                .costPrice(new BigDecimal("1750"))
                .stock(30).emoji("⌨️")
                .badge(Product.Badge.NEW)
                .category(general).active(true).build();
        gamingKeyboard = productRepository.save(gamingKeyboard);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(gamingKeyboard).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187834/Screenshot_2024-09-01_232919_axwc0v.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(gamingKeyboard).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187834/2_bz9btq.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(gamingKeyboard).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187833/3_lwpxmd.png").isPrimary(false).sortOrder(3).build(),
                ProductImage.builder().product(gamingKeyboard).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187831/Screenshot_2024-09-01_233227_ucwayd.png").isPrimary(false).sortOrder(4).build()
        ));


        // ══════════════════════════════════════════════════════════════
        // KITCHENWARE
        // ══════════════════════════════════════════════════════════════

        // 13. Dish Rack
        Product dishRack = Product.builder()
                .name("2-Tier Stainless Steel Dish Rack with Glass Holder")
                .description("Double-tier stainless steel dish drainer with dedicated slots for plates, cups, glasses, and utensils. Chrome finish is rust-resistant and easy to wipe clean. Includes a drip tray to keep counters dry. Compact yet spacious enough for a full family's dishes.")
                .specifications("""
                    Material         : Stainless steel — chrome finish
                    Tiers            : 2-tier with utensil and glass holder
                    Drip Tray        : Included
                    Rust Resistant   : Yes
                    Easy Clean       : Yes
                    Best For         : Plates, cups, glasses, cutlery
                    """)
                .retailPrice(new BigDecimal("3250"))
                .wholesalePrice(new BigDecimal("2500"))
                .costPrice(new BigDecimal("2300"))
                .stock(25).emoji("🍽️")
                .category(kitchenware).active(true).build();
        dishRack = productRepository.save(dishRack);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(dishRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187710/real_siverDIsh_retwvn.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(dishRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187712/dish03_oqhrfo.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(dishRack).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777187714/dish02_cnuiql.png").isPrimary(false).sortOrder(3).build()
        ));

        // 14. 3-in-1 Roto Peeler
        Product rotoPeeler = Product.builder()
                .name("3-in-1 Roto Peeler")
                .description("Versatile rotary peeler with three interchangeable blades — soft fruit blade for tomatoes and kiwis, julienne blade for thin strips, and wavy blade for crinkle-cut chips. Ergonomic handle for comfortable grip. A compact kitchen essential for everyday meal prep.")
                .specifications("""
                    Blades           : 3 — Soft Fruit, Julienne, Wavy
                    Soft Fruit Blade : Ideal for tomatoes and kiwis
                    Julienne Blade   : Thin strip peeling
                    Wavy Blade       : Crinkle-cut chips
                    Handle           : Ergonomic non-slip grip
                    Available Colors : Orange, Grey, Green, Black
                    """)
                .retailPrice(new BigDecimal("675"))
                .wholesalePrice(new BigDecimal("550"))
                .costPrice(new BigDecimal("450"))
                .stock(70).emoji("🥕")
                .badge(Product.Badge.NEW)
                .category(kitchenware).active(true).build();
        rotoPeeler = productRepository.save(rotoPeeler);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(rotoPeeler).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188279/1.4_ixkldv.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(rotoPeeler).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188284/5_z1c3rj.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(rotoPeeler).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188287/4_p0qwxu.png").isPrimary(false).sortOrder(3).build()
        ));

        // 15. SF-400 Kitchen Scale
        Product kitchenScale = Product.builder()
                .name("SF-400 Digital Kitchen Scale")
                .description("Compact and accurate digital kitchen scale for precise ingredient measurement. LCD display with easy-to-read numbers. Tare function lets you zero out container weight instantly. Ideal for baking, cooking, and portion control. Batteries included, ready to use out of the box.")
                .specifications("""
                    Model            : SF-400
                    Capacity         : 5kg maximum
                    Precision        : 1g
                    Display          : LCD digital
                    Functions        : Tare / Zero function
                    Units            : g / kg / oz / lb
                    Power            : 2x AA batteries (included)
                    """)
                .retailPrice(new BigDecimal("850"))
                .wholesalePrice(new BigDecimal("700"))
                .costPrice(new BigDecimal("600"))
                .stock(50).emoji("⚖️")
                .category(kitchenware).active(true).build();
        kitchenScale = productRepository.save(kitchenScale);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(kitchenScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188423/an.2_ccrfxh.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(kitchenScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188425/an.3_svpra1.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(kitchenScale).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188426/an.1_nfpal5.jpg").isPrimary(false).sortOrder(3).build()
        ));


        // ══════════════════════════════════════════════════════════════
        // ALUMINIUM
        // ══════════════════════════════════════════════════════════════

        // 16. Pittu Bambu
        Product pittuBambu = Product.builder()
                .name("Aluminium Pittu Bambu")
                .description("Traditional Sri Lankan pittu maker crafted from premium quality aluminium. Lightweight yet durable with a lustrous finish. Ergonomically designed handle for a comfortable cooking grip. Eco-friendly and hygienic — a must-have in every Sri Lankan kitchen.")
                .specifications("""
                    Material         : Premium aluminium
                    Finish           : Luster polish
                    Design           : Traditional Sri Lankan pittu mould
                    Handle           : Ergonomically designed
                    Eco-Friendly     : Yes
                    Hygienic         : Yes — easy to clean
                    """)
                .retailPrice(new BigDecimal("2850"))
                .wholesalePrice(new BigDecimal("2400"))
                .costPrice(new BigDecimal("2250"))
                .stock(30).emoji("🍚")
                .category(aluminium).active(true).build();
        pittuBambu = productRepository.save(pittuBambu);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(pittuBambu).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189349/pittuBambu_ftkfkb.png").isPrimary(true).sortOrder(1).build()
        ));

        // 17. Aluminium Thachchi (Wok)
        Product thachchi = Product.builder()
                .name("Aluminium Polish Thachchi (30cm)")
                .description("Traditional polished aluminium cooking wok — a staple in Sri Lankan kitchens. 30cm diameter and 14cm depth provides ample space for curries, stir-fries, and deep frying. Lightweight with double loop handles for easy handling. Available in multiple sizes.")
                .specifications("""
                    Material         : Polished aluminium
                    Diameter         : 30cm
                    Depth            : 14cm
                    Handles          : Dual loop handles
                    Best For         : Curries, stir-fry, deep frying
                    Available Sizes  : Multiple sizes available
                    """)
                .retailPrice(new BigDecimal("1000"))
                .wholesalePrice(new BigDecimal("850"))
                .costPrice(new BigDecimal("750"))
                .stock(40).emoji("🥘")
                .category(aluminium).active(true).build();
        thachchi = productRepository.save(thachchi);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(thachchi).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189581/aluminum_thachi_uq29vc.png").isPrimary(true).sortOrder(1).build()
        ));

        // 18. Aluminium Rotti Plate
        Product rottiPlate = Product.builder()
                .name("Aluminium Rotti Thattu (Griddle Plate)")
                .description("Traditional high-quality aluminium rotti plate for making authentic Sri Lankan rotti and flatbreads. Flat cooking surface with a sturdy side handle for easy manoeuvrability. Retains heat evenly for perfectly cooked rotti every time. A classic kitchen essential for every home.")
                .specifications("""
                    Material         : High-quality aluminium
                    Type             : Flat griddle plate with side handle
                    Best For         : Rotti, flatbreads, dosa
                    Heat Retention   : Even heat distribution
                    Handle           : Solid cast side handle
                    """)
                .retailPrice(new BigDecimal("950"))
                .wholesalePrice(new BigDecimal("700"))
                .costPrice(new BigDecimal("600"))
                .stock(35).emoji("🫓")
                .category(aluminium).active(true).build();
        rottiPlate = productRepository.save(rottiPlate);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(rottiPlate).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189713/rotti_thattu_rl3y2g.png").isPrimary(true).sortOrder(1).build()
        ));


        // ══════════════════════════════════════════════════════════════
        // PLASTIC
        // ══════════════════════════════════════════════════════════════

        // 19. Well Bucket (Plastic)
        Product wellBucket = Product.builder()
                .name("Plastic Well Bucket (Without Lid)")
                .description("Sturdy plastic well bucket for household and outdoor water use. Lightweight, durable, and rust-free with a strong steel wire handle. Suitable for wells, garden use, and general water carrying tasks. Available in assorted colours.")
                .specifications("""
                    Material         : Heavy-duty plastic
                    Handle           : Steel wire
                    Lid              : Not included
                    Use              : Well, garden, general household
                    Rust-Free        : Yes
                    """)
                .retailPrice(new BigDecimal("450"))
                .wholesalePrice(new BigDecimal("330"))
                .costPrice(new BigDecimal("280"))
                .stock(100).emoji("🪣")
                .category(plastic).active(true).build();
        wellBucket = productRepository.save(wellBucket);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(wellBucket).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188689/1_qoqefm.jpg").isPrimary(true).sortOrder(1).build()
        ));

        // 20. Large Plastic Water Jug
        Product waterJug = Product.builder()
                .name("Large Plastic Water Jug with Lid")
                .description("Large-capacity plastic water jug with a secure snap-on lid and easy-pour spout. Lightweight and unbreakable — ideal for everyday home use. Keeps water, juice, and beverages fresh and covered. Available in bright assorted colours.")
                .specifications("""
                    Material         : Food-grade plastic
                    Lid              : Snap-on secure lid
                    Spout            : Easy-pour design
                    Use              : Water, juice, beverages
                    Lightweight      : Yes — unbreakable
                    """)
                .retailPrice(new BigDecimal("550"))
                .wholesalePrice(new BigDecimal("380"))
                .costPrice(new BigDecimal("330"))
                .stock(80).emoji("🥤")
                .category(plastic).active(true).build();
        waterJug = productRepository.save(waterJug);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(waterJug).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777188987/DSC_31_gtsvbm.jpg").isPrimary(true).sortOrder(1).build()
        ));

        // 21. U Bag Plastic
        Product uBag = Product.builder()
                .name("Plastic U Bag (Shopping Basket)")
                .description("Lightweight and flexible plastic shopping basket with a woven lattice design and sturdy round handles. Perfect for market trips, beach outings, or home storage. Durable, easy to clean, and available in vibrant colours.")
                .specifications("""
                    Material         : Flexible plastic
                    Design           : Woven lattice pattern
                    Handles          : Dual round carry handles
                    Use              : Shopping, market, home storage
                    Easy Clean       : Yes
                    """)
                .retailPrice(new BigDecimal("275"))
                .wholesalePrice(new BigDecimal("180"))
                .costPrice(new BigDecimal("140"))
                .stock(150).emoji("🛍️")
                .category(plastic).active(true).build();
        uBag = productRepository.save(uBag);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(uBag).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189084/DSC_77-768x514_qqq4mq.jpg").isPrimary(true).sortOrder(1).build()
        ));


        // ══════════════════════════════════════════════════════════════
        // GIFT ITEMS
        // ══════════════════════════════════════════════════════════════

        // 22. Glass Pitcher with 4 Cups Set
        Product glassPitcher = Product.builder()
                .name("Glass Pitcher with 4 Cups Set (1.3L)")
                .description("Elegant ribbed glass water carafe set with 4 matching cups — perfect for dining tables, gifts, and entertaining. Push-type lid with smooth pour spout prevents spills. Wide mouth for easy cleaning and filling. Suitable for water, juice, iced tea, and more.")
                .specifications("""
                    Material         : Clear glass
                    Capacity         : 1300ml pitcher + 4 cups
                    Lid              : Push-type, spill-proof
                    Dimensions       : Pitcher — Ø11.6cm x H20.2cm
                    Dishwasher Safe  : Yes
                    Best For         : Dining, gifting, entertaining
                    """)
                .retailPrice(new BigDecimal("2250"))
                .wholesalePrice(new BigDecimal("2000"))
                .costPrice(new BigDecimal("1800"))
                .stock(25).emoji("🫗")
                .badge(Product.Badge.NEW)
                .category(giftItems).active(true).build();
        glassPitcher = productRepository.save(glassPitcher);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(glassPitcher).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189976/glass_set_1_jkly0x.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(glassPitcher).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189969/glass_set_3_k9rnik.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(glassPitcher).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777189977/glass_set_2_xximob.png").isPrimary(false).sortOrder(3).build()
        ));

        // 23. 6pcs Drinking Glass Set
        Product drinkingGlassSet = Product.builder()
                .name("6-Piece Drinking Glass Set")
                .description("Set of 6 classic ribbed drinking glasses — elegant, durable, and perfect for everyday use or gifting. Clear glass design suits any table setting. Dishwasher safe and easy to store. Ideal for water, juice, cold drinks, and more.")
                .specifications("""
                    Pieces           : 6
                    Material         : Clear glass
                    Design           : Ribbed classic style
                    Dishwasher Safe  : Yes
                    Best For         : Water, juice, cold beverages
                    Use              : Everyday use or gift set
                    """)
                .retailPrice(new BigDecimal("1450"))
                .wholesalePrice(new BigDecimal("950"))
                .costPrice(new BigDecimal("800"))
                .stock(40).emoji("🥃")
                .category(giftItems).active(true).build();
        drinkingGlassSet = productRepository.save(drinkingGlassSet);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(drinkingGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190153/6pcs_glass_2_ej8brf.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(drinkingGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190193/6pcs_glass_1_z1y0cm.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(drinkingGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190189/6pcs_glass_3_nmkdqy.png").isPrimary(false).sortOrder(3).build()
        ));

        // 24. 6pcs Square Base Heavy Glass Set
        Product heavyGlassSet = Product.builder()
                .name("6-Piece Square Base Heavy Glass Set")
                .description("Set of 6 premium thick-walled square base drinking glasses — solid, stylish, and built to last. Dishwasher and microwave safe. The heavy base design prevents tipping and adds an upmarket look to any table. Perfect for gifting or home use.")
                .specifications("""
                    Pieces           : 6
                    Material         : Thick tempered glass
                    Design           : Square heavy base
                    Capacity         : 300ml (13.5x6.5cm) / 225ml (11.5x6.2cm)
                    Dishwasher Safe  : Yes
                    Microwave Safe   : Yes
                    Non-Toxic        : Yes
                    """)
                .retailPrice(new BigDecimal("2450"))
                .wholesalePrice(new BigDecimal("2050"))
                .costPrice(new BigDecimal("1800"))
                .stock(30).emoji("🥃")
                .badge(Product.Badge.NEW)
                .category(giftItems).active(true).build();
        heavyGlassSet = productRepository.save(heavyGlassSet);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(heavyGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190385/heavy_glass_set_2_tq1zg0.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(heavyGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190368/heavy_glass_set_1_j9kvjl.png").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(heavyGlassSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190353/heavy_glass_set_3_o7a8av.png").isPrimary(false).sortOrder(3).build()
        ));

        // 25. 6pcs Ceramic Cup Set
        Product ceramicCupSet = Product.builder()
                .name("6-Piece Ceramic Tea Cup Set (Real Brand)")
                .description("Elegant set of 6 fine porcelain tea cups with a classic gold-trimmed traditional design. Made for Sri Lanka by REAL — fine porcelainware with a beautiful luster finish. Capacity 160cc per cup. A thoughtful gift for any occasion — weddings, housewarmings, and more.")
                .specifications("""
                    Brand            : REAL Fine Porcelainware
                    Pieces           : 6 cups
                    Material         : Fine porcelain / ceramic
                    Capacity         : 160cc per cup
                    Design           : Traditional gold-trim pattern
                    Best For         : Tea, coffee, gifting
                    Origin           : Made for Sri Lanka
                    """)
                .retailPrice(new BigDecimal("1950"))
                .wholesalePrice(new BigDecimal("1800"))
                .costPrice(new BigDecimal("1650"))
                .stock(35).emoji("☕")
                .badge(Product.Badge.BEST_SELLER)
                .category(giftItems).active(true).build();
        ceramicCupSet = productRepository.save(ceramicCupSet);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(ceramicCupSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190575/ceramic_cup_1_uhzn9g.png").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(ceramicCupSet).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777190623/ceramic_cup_2_s6dhew.png").isPrimary(false).sortOrder(2).build()
        ));

        // ══════════════════════════════════════════════════════════════
// UMBRELLAS
// ══════════════════════════════════════════════════════════════

// Rainco 27″ Multi-Colour Gents Umbrella (2573)
        Product umbrella2573 = Product.builder()
                .name("Rainco 27″ Multi-Colour Gents Umbrella (2573)")
                .description("Add a touch of colour to rainy days with this vibrant Rainco gents umbrella. Built with a 16-rib structure for excellent wind resistance and long-lasting durability. High-quality polyester fabric offers reliable water resistance. Ample 121cm open diameter keeps you well covered in any weather.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 2573
                Size             : 27″ (69cm)
                Ribs             : 16
                Diameter         : 121cm open
                Fabric           : Polyester
                Fold Type        : 1-fold
                Weight           : 550g
                """)
                .retailPrice(new BigDecimal("1650"))
                .wholesalePrice(new BigDecimal("1400"))
                .costPrice(new BigDecimal("1250"))
                .stock(30).emoji("☂️")
                .badge(Product.Badge.NEW)
                .category(umbrellas).active(true).build();
        umbrella2573 = productRepository.save(umbrella2573);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella2573).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194558/gent_2573_jqrdth.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella2573).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194559/gent_2573_2_y2fjyj.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella2573).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194560/gent_2573_3_mcaczn.jpg").isPrimary(false).sortOrder(3).build()
        ));

// Rainco 30″ Multi-Colour Gents Umbrella (2583)
        Product umbrella2583 = Product.builder()
                .name("Rainco 30″ Multi-Colour Gents Umbrella (2583)")
                .description("Experience superior coverage with this large Rainco gents umbrella. The 16-rib structure ensures enhanced wind resistance and long-lasting performance. With an impressive 133cm diameter it provides ample coverage for both individual and family use. Lightweight at 569g for easy handling.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 2583
                Size             : 30″ (77cm)
                Ribs             : 16
                Diameter         : 133cm open
                Fabric           : Polyester
                Fold Type        : 1-fold
                Weight           : 569g
                """)
                .retailPrice(new BigDecimal("1890"))
                .wholesalePrice(new BigDecimal("1600"))
                .costPrice(new BigDecimal("1500"))
                .stock(25).emoji("☂️")
                .category(umbrellas).active(true).build();
        umbrella2583 = productRepository.save(umbrella2583);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella2583).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194674/2583_gent_1_gha1mr.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella2583).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194726/2583_gent_2_nbnvmh.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella2583).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194727/2583_gent_3_xzd9z2.jpg").isPrimary(false).sortOrder(3).build()
        ));

// Rainco Kids Telescope Umbrella (11600)
        Product umbrella11600 = Product.builder()
                .name("Rainco Kids Telescope Umbrella (11600)")
                .description("Brighten up your child's rainy day with this fun rainbow-coloured kids umbrella from Rainco. Lightweight at just 290g making it easy for children to carry. Sturdy 8-rib polyester construction provides reliable coverage. The colourful design makes it a favourite for kids of all ages.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 11600
                Size             : 42cm
                Ribs             : 8
                Diameter         : 74cm open
                Fabric           : Polyester
                Fold Type        : 1-fold
                Weight           : 290g
                """)
                .retailPrice(new BigDecimal("1090"))
                .wholesalePrice(new BigDecimal("950"))
                .costPrice(new BigDecimal("850"))
                .stock(40).emoji("☂️")
                .category(umbrellas).active(true).build();
        umbrella11600 = productRepository.save(umbrella11600);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella11600).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194837/11600_3_qqs66m.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella11600).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194835/11600_2_karxjb.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella11600).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194833/11600_1_xznufm.jpg").isPrimary(false).sortOrder(3).build()
        ));

// Rainco Junior 15″ Kids Umbrella (1060)
        Product umbrella1060 = Product.builder()
                .name("Rainco Junior 15″ Kids Umbrella (1060)")
                .description("Specially designed for young children, this Rainco Junior umbrella is ultra-lightweight at just 173g. The fun space-themed print makes it a joy to carry. 8-rib polyester construction provides reliable water resistance. Perfect size for small hands with a comfortable curved handle.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 1060
                Size             : 15″ (42cm)
                Ribs             : 8
                Diameter         : 70cm open
                Fabric           : Polyester
                Fold Type        : 1-fold
                Weight           : 173g
                """)
                .retailPrice(new BigDecimal("890"))
                .wholesalePrice(new BigDecimal("800"))
                .costPrice(new BigDecimal("700"))
                .stock(35).emoji("☂️")
                .category(umbrellas).active(true).build();
        umbrella1060 = productRepository.save(umbrella1060);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella1060).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194957/1060_1_donp7g.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella1060).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194955/1060_3_lpisju.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella1060).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777194953/1060_2_muq9q7.jpg").isPrimary(false).sortOrder(3).build()
        ));

// Rainco 24″ Black Gents Umbrella (2561)
        Product umbrella2561 = Product.builder()
                .name("Rainco 24″ Black Gents Umbrella (2561)")
                .description("Stay dry and stylish with this classic Rainco black gents umbrella. Steel frame construction ensures enhanced strength and stability. High-quality polyester fabric offers reliable water resistance. A timeless everyday essential for the modern gentleman.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 2561
                Size             : 24″
                Frame            : Steel
                Fabric           : Polyester
                Fold Type        : 1-fold
                Style            : Classic black
                """)
                .retailPrice(new BigDecimal("1590"))
                .wholesalePrice(new BigDecimal("1500"))
                .costPrice(new BigDecimal("1400"))
                .stock(30).emoji("☂️")
                .category(umbrellas).active(true).build();
        umbrella2561 = productRepository.save(umbrella2561);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella2561).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195061/2561__ejfdnx.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella2561).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195059/2561_3_rghodw.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella2561).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195032/2561_2_qlficc.jpg").isPrimary(false).sortOrder(3).build()
        ));

// Rainco 27″ Black Gents Umbrella (2571)
        Product umbrella2571 = Product.builder()
                .name("Rainco 27″ Black Gents Umbrella (2571)")
                .description("A timeless classic built for everyday reliability. The 16-rib structure and double-fold mechanism make this Rainco umbrella both durable and easy to use. Generous 121cm open diameter provides ample coverage. Sturdy yet easy to carry at 510g — the dependable choice for any weather.")
                .specifications("""
                Brand            : Rainco
                Item Code        : 2571
                Size             : 27″ (69cm)
                Ribs             : 16
                Diameter         : 121cm open
                Fabric           : Polyester
                Fold Type        : 1/Double fold
                Weight           : 510g
                """)
                .retailPrice(new BigDecimal("1690"))
                .wholesalePrice(new BigDecimal("1600"))
                .costPrice(new BigDecimal("1500"))
                .stock(30).emoji("☂️")
                .category(umbrellas).active(true).build();
        umbrella2571 = productRepository.save(umbrella2571);
        productImageRepository.saveAll(List.of(
                ProductImage.builder().product(umbrella2571).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195065/2571_1_fygmul.jpg").isPrimary(true).sortOrder(1).build(),
                ProductImage.builder().product(umbrella2571).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195067/2571_2_tvsnmw.jpg").isPrimary(false).sortOrder(2).build(),
                ProductImage.builder().product(umbrella2571).imageUrl("https://res.cloudinary.com/dbet3dqvh/image/upload/v1777195063/2571_3_yr3qxi.jpg").isPrimary(false).sortOrder(3).build()
        ));


        System.out.println("✅ All products initialized successfully");
    }

    private void generateEmbeddings() {
        long productsWithoutEmbedding = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE embedding IS NULL AND active = true",
                Long.class
        );


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