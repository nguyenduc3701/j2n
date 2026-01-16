# Backend For Frontend Service (bff-srv)

## English Version

### 1. Title & Overview

The **BFF Service** acts as a simplified interface for frontend clients. It aggregates and orchestrates data from multiple downstream services to provide clean, optimized APIs for the UI.

### 2. Responsibilities

- API Orchestration (calling multiple services to fulfill a single frontend request).
- Data mapping and DTO transformation for UI needs.
- Proxying file uploads and downloads.
- Handling frontend-specific business logic.

### 3. Architecture Overview

A Spring Boot application that calls other microservices through the API Gateway (or directly in dev) and serves the frontend.

### 4. API Endpoints

- `/api/bff/login`, `/api/bff/register`: Orchestrates auth with `api-gateway`.
- `/api/bff/users/**`: User management bridge.
- `/api/bff/image/**`: Image upload/download bridge.

### 5. Messaging / Events

No direct messaging. Realy on REST calls to downstream services which may trigger events.

### 6. Configuration

- `server.port`: 8180
- `api-gateway.base-url`: URL of the API Gateway.

### 7. Run Locally

Run `BffSrvApplication`. Ensure upstream services or Gateway are running.

### 8. Error Handling

Standardized error wrapping from downstream services.

### 9. Security

Includes `FROM-BFF: true` header in requests to Gateway to identify itself.

### 10. Limitations & Notes

Should not store data; mostly stateless proxying and orchestration.

---

## Phiên bản tiếng Việt

### 1. Tiêu đề & Tổng quan

**BFF Service** đóng vai trò là giao diện đơn giản hóa cho các frontend client. Nó tổng hợp và điều phối dữ liệu từ nhiều dịch vụ downstream để cung cấp các API sạch và tối ưu cho giao diện người dùng (UI).

### 2. Trách nhiệm

- Điều phối API (gọi nhiều dịch vụ để đáp ứng một yêu cầu duy nhất từ frontend).
- Ánh xạ dữ liệu và chuyển đổi DTO cho nhu cầu của UI.
- Proxy cho việc upload và download tệp tin.
- Xử lý các logic nghiệp vụ đặc thù của frontend.

### 3. Tổng quan kiến trúc

Một ứng dụng Spring Boot gọi các microservices khác thông qua API Gateway (hoặc gọi trực tiếp trong môi trường dev) và phục vụ frontend.

### 4. Các API chính

- `/api/bff/login`, `/api/bff/register`: Điều phối xác thực với `api-gateway`.
- `/api/bff/users/**`: Cầu nối quản lý người dùng.
- `/api/bff/image/**`: Cầu nối upload/download ảnh.

### 5. Messaging / Events

Không có messaging trực tiếp. Dựa vào các lời gọi REST tới các dịch vụ downstream, những dịch vụ này có thể kích hoạt các sự kiện.

### 6. Cấu hình

- `server.port`: 8180
- `api-gateway.base-url`: URL của API Gateway.

### 7. Cách chạy local

Chạy class `BffSrvApplication`. Đảm bảo các dịch vụ upstream hoặc Gateway đang chạy.

### 8. Xử lý lỗi

Đóng gói các lỗi chuẩn hóa từ các dịch vụ downstream.

### 9. Bảo mật

Chèn header `FROM-BFF: true` trong các yêu cầu gửi tới Gateway để định danh chính nó.

### 10. Giới hạn & Lưu ý

Không nên lưu trữ dữ liệu; chủ yếu là proxy stateless và điều phối.
