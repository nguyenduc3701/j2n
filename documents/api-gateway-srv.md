# API Gateway Service (api-gateway-srv)

## English Version

### 1. Title & Overview

The **API Gateway Service** is the entry point for all external requests to the J2N backend system. It acts as a reverse proxy, routing requests to appropriate downstream services while handling authentication and cross-cutting concerns.

### 2. Responsibilities

- Routing requests based on path predicates.
- JWT Authentication and token validation.
- Header manipulation (e.g., adding `X-User-Id`, `X-Role-Id`).
- Standardized error response for unauthorized or malformed requests.

### 3. Architecture Overview

Built on **Spring Cloud Gateway** and **Spring WebFlux** (Reactive). It sits between the clients (or BFF) and the microservices.

### 4. API Endpoints

- `/api/auth/**` -> Proxies to `auth-srv`.
- `/api/image/**` -> Proxies to `image-srv`.

### 5. Messaging / Events

No direct messaging/event interaction.

### 6. Configuration

- `server.port`: 8181
- `spring.cloud.gateway.routes`: Defined in `application.yml`.
- `jwt.secret`: Shared secret for token validation.

### 7. Run Locally

Run via Maven: `./mvnw spring-boot:run` or through your IDE's run configuration for `ApiGatewaySrvApplication`.

### 8. Error Handling

Uses `ResponseFactory` to return consistent JSON errors (e.g., 401 Unauthorized) when JWT validation fails.

### 9. Security

Implements `JwtAuthFilter` which extracts the Bearer token, validates it, and populates security headers for internal services.

### 10. Limitations & Notes

Being reactive, blocking operations should be avoided in filters.

---

## Phiên bản tiếng Việt

### 1. Tiêu đề & Tổng quan

**API Gateway Service** là cổng vào duy nhất cho tất cả các yêu cầu từ bên ngoài vào hệ thống backend J2N. Nó đóng vai trò là một reverse proxy, điều hướng các yêu cầu đến các dịch vụ phía sau (downstream services) đồng thời xử lý xác thực và các vấn đề chung của hệ thống.

### 2. Trách nhiệm

- Điều hướng request dựa trên đường dẫn (path predicates).
- Xác thực JWT và kiểm tra tính hợp lệ của token.
- Thao tác với header (ví dụ: thêm `X-User-Id`, `X-Role-Id` trước khi chuyển tiếp).
- Chuẩn hóa phản hồi lỗi cho các yêu cầu không hợp lệ hoặc không có quyền truy cập.

### 3. Tổng quan kiến trúc

Xây dựng trên nền tảng **Spring Cloud Gateway** và **Spring WebFlux** (Reactive). Nó nằm giữa các client (hoặc BFF) và các microservices.

### 4. Các API chính

- `/api/auth/**` -> Chuyển tiếp tới `auth-srv`.
- `/api/image/**` -> Chuyển tiếp tới `image-srv`.

### 5. Messaging / Events

Không tương tác trực tiếp với messaging/event.

### 6. Cấu hình

- `server.port`: 8181
- `spring.cloud.gateway.routes`: Định nghĩa trong `application.yml`.
- `jwt.secret`: Secret key dùng chung để validate token.

### 7. Cách chạy local

Chạy qua Maven: `./mvnw spring-boot:run` hoặc sử dụng cấu hình chạy của IDE cho class `ApiGatewaySrvApplication`.

### 8. Xử lý lỗi

Sử dụng `ResponseFactory` để trả về lỗi JSON thống nhất (ví dụ: 401 Unauthorized) khi việc xác thực JWT thất bại.

### 9. Bảo mật

Triển khai `JwtAuthFilter` để trích xuất Bearer token, xác thực nó, và điền các header bảo mật cho các dịch vụ nội bộ.

### 10. Giới hạn & Lưu ý

Vì là reactive, nên tránh các thao tác chặn (blocking operations) trong các filter.
