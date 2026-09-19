# Josh Jewellery - Enterprise Backend (Spring Boot 3 + PostgreSQL)

A complete, production-ready backend architecture for **Josh Jewellery**, built using Java 21, Spring Boot 3.x, Spring Security with JWT authentication, Spring Data JPA, PostgreSQL, and Flyway migration scripts.

---

## 🛠️ Tech Stack & Architecture

- **Java 21 & Spring Boot 3.2.x**
- **Spring Security & JWT**: Custom Stateless JWT Filter with BCrypt Hashing and Role-Based Access Control (`ROLE_CUSTOMER`, `ROLE_ADMIN`).
- **PostgreSQL Database**: UUID Primary Keys, Flyway Database Migrations (`V1__init_schema.sql`, `V2__seed_data.sql`), Optimistic Locking, Soft Delete support.
- **Single Secure Admin**: On application startup (`AdminInitializerOnStartup`), automatically initializes a single Admin account if one does not exist using environment variables (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NAME`). No registration API exists for admin accounts.
- **REST APIs**: Full CRUD coverage for Products, Categories, Cart, Wishlist, Orders, Reviews, Coupons, Live Gold Price, and Admin Dashboard.
- **Swagger / OpenAPI 3**: Interactive API Documentation at `/swagger-ui.html`.
- **Docker**: Multi-stage `Dockerfile` and `docker-compose.yml` for PostgreSQL and Spring Boot container orchestration.

---

## 🚀 Local Setup & Running Instructions

### 1. Prerequisites
- **Java 21 JDK**
- **Maven 3.9+**
- **PostgreSQL 16** (or Docker)

### 2. Running with Docker Compose (Recommended)
```bash
cd backend
docker-compose up --build -d
```
This spins up PostgreSQL on port `5432` and the Spring Boot application on port `8080`.

### 3. Running Locally via Maven
Ensure PostgreSQL is running locally with database `josh_jewellery`, then execute:
```bash
cd backend
mvn clean spring-boot:run
```

---

## 🔐 Default Admin Credentials

Default credentials initialized automatically on application startup:
- **Email**: `admin@joshjewellery.com` (Configurable via `ADMIN_EMAIL`)
- **Password**: `AdminPassword@123` (Configurable via `ADMIN_PASSWORD`)
- **Role**: `ROLE_ADMIN`

---

## 📖 API Documentation & Swagger UI

Once the application is running, access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```
Use the `Bearer <JWT_TOKEN>` authorization header for protected customer & admin endpoints.

---

## ⚡ Core REST Endpoints

### 🔑 Auth APIs
- `POST /api/auth/register` - Register customer account
- `POST /api/auth/login` - Authenticate customer or admin & return JWT
- `POST /api/auth/logout` - Invalidate session

### 💍 Products & Categories
- `GET /api/products` - Filterable paginated catalog (category, metal, purity, search)
- `GET /api/products/{id}` - Product details
- `GET /api/products/slug/{slug}` - Product details by slug
- `GET /api/products/bestsellers` - Best selling items
- `GET /api/categories` - List all active categories

### 🛒 Cart & Wishlist
- `GET /api/cart` - View user cart
- `POST /api/cart` - Add item to cart with ring size & custom engraving
- `PUT /api/cart/items/{itemId}` - Update item quantity
- `DELETE /api/cart/items/{itemId}` - Remove item from cart
- `GET /api/wishlist` - Customer wishlist items
- `POST /api/wishlist/{productId}` - Save product to wishlist

### 📦 Orders & Gold Rates
- `POST /api/orders` - Place new order from cart
- `GET /api/orders` - View order history
- `GET /api/gold-price` - Get live market rates for 24K, 22K, 18K, 14K gold

### 🛡️ Admin APIs (ROLE_ADMIN Only)
- `GET /api/admin/dashboard` - Total revenue, customer counts, pending orders
- `POST /api/admin/products` - Create new product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Soft delete product
- `PUT /api/admin/orders/{id}/status` - Update order status (`CONFIRMED`, `SHIPPED`, `DELIVERED`)
- `PUT /api/admin/gold-price` - Update daily gold rates

---

## 🧪 Running Unit & Integration Tests
```bash
mvn clean test
```
Includes JUnit 5 & Mockito test suites for services, security, and controllers.
