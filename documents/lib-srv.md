# Shared Library (lib-srv)

## English Version

### 1. Title & Overview

**lib-srv** is a shared module containing common DTOs, utilities, constants, and messaging configurations used across all microservices in the J2N ecosystem.

### 2. Responsibilities

- Providing standardized base response and request structures.
- Sharing event contracts (POJOs) for RabbitMQ.
- Common utils for paging, response generation, and password encoding.
- Centralized RabbitMQ and Jackson configurations.

### 3. Architecture Overview

A plain Maven module (not a standalone service) imported as a dependency by other services.

### 4. API Endpoints

None (Library module).

### 5. Messaging / Events

Defines the base `UserAvatarUploadEvent` and `BaseEventPublisher` used by other services.

### 6. Configuration

Contains `RabbitTemplateConfig` and `BaseRabbitConfig` to ensure all services use JSON serialization uniformly.

### 7. Run Locally

Build locally using `mvn clean install` so other services can resolve the dependency.

### 8. Error Handling

Provides standardized `MessageEnum` and `ResponseFactory`.

### 9. Security

Provides `PasswordUtil` using BCrypt for consistent password handling.

### 10. Limitations & Notes

Changes here often require rebuilding and restarting all dependent services.

---

## Phiên bản tiếng Việt

### 1. Tiêu đề & Tổng quan

**lib-srv** là một module dùng chung chứa các DTO, tiện ích (utils), hằng số (constants) và cấu hình messaging chung được sử dụng trên tất cả các microservices trong hệ sinh thái J2N.

### 2. Trách nhiệm

- Cung cấp cấu trúc phản hồi (response) và yêu cầu (request) cơ bản chuẩn hóa.
- Chia sẻ các hợp đồng sự kiện (POJOs) cho RabbitMQ.
- Các tiện ích chung cho phân trang, tạo phản hồi và mã hóa mật khẩu.
- Tập trung các cấu hình RabbitMQ và Jackson.

### 3. Tổng quan kiến trúc

Một Maven module thuần túy (không phải là dịch vụ độc lập) được import dưới dạng dependency bởi các dịch vụ khác.

### 4. Các API chính

Không có (Module thư viện).

### 5. Messaging / Events

Định nghĩa `UserAvatarUploadEvent` cơ bản và `BaseEventPublisher` được sử dụng bởi các dịch vụ khác.

### 6. Cấu hình

Chứa `RabbitTemplateConfig` và `BaseRabbitConfig` để đảm bảo tất cả các dịch vụ sử dụng JSON serialization đồng nhất.

### 7. Cách chạy local

Build local bằng lệnh `mvn clean install` để các dịch vụ khác có thể nhận diện dependency này.

### 8. Xử lý lỗi

Cung cấp `MessageEnum` và `ResponseFactory` chuẩn hóa.

### 9. Bảo mật

Cung cấp `PasswordUtil` sử dụng BCrypt để xử lý mật khẩu nhất quán.

### 10. Giới hạn & Lưu ý

Các thay đổi ở đây thường yêu cầu phải build lại và khởi động lại tất cả các dịch vụ phụ thuộc.
