# Ghanim Enterprises API

REST API for the Ghanim Enterprises e-commerce platform built with **Spring Boot 3**, **Spring Security**, **JWT authentication**, and **PostgreSQL** — supporting retail and wholesale pricing with role-based access control.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.x-green?style=flat-square&logo=springboot)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-0.12.5-black?style=flat-square&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apachemaven)

---

## 🔗 Related Repositories
- **Frontend**: [ecommerce-angular-springboot](https://github.com/Inscode/ecommerce-angular-springboot)

---

## ✨ Features

### API Features
- RESTful API with proper HTTP methods and status codes
- JWT-based stateless authentication
- Role-based access control — Retail, Wholesale, Admin
- Retail and wholesale pricing per product
- Product and category management
- Order management
- Customer management
- AI smart search with pgvector (planned)
- AI chatbot integration (planned)

### Security Features
- Spring Security with JWT filter chain
- Password encryption with BCrypt
- CORS configuration for Angular frontend
- Protected endpoints by role
- Token expiration and refresh (planned)

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.5.x | Framework |
| Spring Security | 6.x | Authentication and authorization |
| Spring Data JPA | 3.x | Database ORM |
| PostgreSQL | 16 | Primary database |
| Hibernate | 6.x | JPA implementation |
| JWT (jjwt) | 0.12.5 | Token generation and validation |
| Lombok | Latest | Reduce boilerplate code |
| Maven | 3.8+ | Build tool |
| pgvector | Latest | Vector search for AI features (planned) |

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL 16+

### Database Setup

Open pgAdmin or psql and run:

```sql
CREATE DATABASE ghanim_db;
```

### Configuration

Copy the example properties file and fill in your values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Open `application.properties` and update:

```properties
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
jwt.secret=YOUR_JWT_SECRET_MINIMUM_32_CHARACTERS
```

### Run the Application

```bash
# Install dependencies and run
mvn spring-boot:run
```

The API will start at `http://localhost:8080`

### Build for Production

```bash
mvn clean package -DskipTests
java -jar target/api-0.0.1-SNAPSHOT.jar
```

---

## 📁 Project Structure

```
src/main/java/lk/ghanim/api/
├── config/          # Security config, CORS config, app config
├── controller/      # REST controllers — auth, product, order, user
├── dto/             # Data transfer objects — request and response
├── entity/          # JPA entities — User, Product, Order, Category
├── exception/       # Global exception handler and custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JWT util, UserDetailsService
└── service/         # Business logic — auth, product, order services
```

---

## 🌐 API Endpoints

### Auth
```
POST   /api/auth/register     Register new user
POST   /api/auth/login        Login and receive JWT token
```

### Products
```
GET    /api/products          Get all products (price based on role)
GET    /api/products/{id}     Get product by ID
GET    /api/products/category/{slug}  Get products by category
POST   /api/products          Add product (Admin only)
PUT    /api/products/{id}     Update product (Admin only)
DELETE /api/products/{id}     Delete product (Admin only)
```

### Categories
```
GET    /api/categories        Get all categories
POST   /api/categories        Add category (Admin only)
```

### Orders
```
GET    /api/orders            Get all orders (Admin only)
GET    /api/orders/my         Get current user orders
POST   /api/orders            Place new order
PUT    /api/orders/{id}/status  Update order status (Admin only)
```

### Users
```
GET    /api/users             Get all users (Admin only)
GET    /api/users/me          Get current user profile
PUT    /api/users/me          Update current user profile
```

---

## 🔐 Authentication

The API uses JWT Bearer token authentication.

**Login to get token:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@ghanim.lk",
  "password": "admin123"
}
```

**Use token in requests:**
```bash
GET /api/products
Authorization: Bearer YOUR_JWT_TOKEN
```

**User Roles:**

| Role | Access |
|---|---|
| RETAIL | Browse products with retail prices, place orders |
| WHOLESALE | Browse products with wholesale prices, place bulk orders |
| ADMIN | Full access — manage products, orders, users |

---

## 💰 Retail vs Wholesale Pricing

The core feature of this API. Each product has two prices stored in the database. The API returns the correct price based on the authenticated user's role:

- `ROLE_RETAIL` → returns `retailPrice`
- `ROLE_WHOLESALE` → returns `wholesalePrice`
- Unauthenticated → returns `retailPrice` (public browsing)

---

## 🗺️ Roadmap

- [x] Spring Boot project setup
- [x] PostgreSQL database connection
- [x] Project folder structure
- [x] JWT dependency configuration
- [ ] Database entities — User, Product, Category, Order
- [ ] Spring Security configuration
- [ ] JWT utility and filter
- [ ] Auth controller — register and login
- [ ] Product controller with role-based pricing
- [ ] Category controller
- [ ] Order controller
- [ ] Connect Angular frontend to API
- [ ] pgvector setup for smart search
- [ ] OpenAI embeddings integration
- [ ] Claude API chatbot integration
- [ ] Deployment

---

## 🧪 Testing the API

Use **Postman** or **Bruno** to test endpoints.

Import the collection (coming soon) or test manually:

```bash
# Health check
GET http://localhost:8080/api/health

# Login
POST http://localhost:8080/api/auth/login
Body: { "email": "admin@ghanim.lk", "password": "admin123" }

# Get products (no auth required)
GET http://localhost:8080/api/products
```

---

## 👨‍💻 Author

Built by **Insaf** as part of a full-stack portfolio project.

- Frontend: Angular 21 with signals, lazy loading, and role-based UI
- Backend: Spring Boot 3 with JWT, JPA, and PostgreSQL
- AI: Smart search with pgvector and chatbot with Claude API

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).