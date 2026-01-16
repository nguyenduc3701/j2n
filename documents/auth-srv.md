# Authentication Service (auth-srv)

## English Version

### 1. Title & Overview

The **Authentication Service** manages user accounts, roles, permissions, and session issuance (JWT). It serves as the source of truth for user identity.

### 2. Responsibilities

- User registration and login.
- Role-Based Access Control (RBAC) management.
- Password hashing and validation.
- Consuming user-related events (e.g., updating profile via events).

### 3. Architecture Overview

A Spring Boot application using Spring Security and JPA (MySQL). It communicates with other services via RabbitMQ.

### 4. API Endpoints

- `/auth/login`: Authenticate and return JWT.
- `/auth/register`: Create new account.
- `/auth/users/me`: Get current user info.
- `/auth/roles`, `/auth/permissions`: RBAC management.

### 5. Messaging / Events

- **Consumer**: Listens to `auth.user.avatar.queue` to update user's avatar URL when an image is uploaded.

### 6. Configuration

- `server.port`: 8182
- `spring.datasource.url`: MySQL connection string.
- `jwt.secret`: Secret for signing tokens.

### 7. Run Locally

Requires MySQL and RabbitMQ. Run `AuthSrvApplication`.

### 8. Error Handling

Centralized exception handling via `GlobalExceptionHandler`, returning standardized `BaseResponse`.

### 9. Security

Uses Spring Security with Stateless session. Validates internal headers if necessary.

### 10. Limitations & Notes

User IDs are currently handled as Strings for interoperability.

---

## Phiên bản tiếng Việt

### 1. Tiêu đề & Tổng quan

**Authentication Service** quản lý tài khoản người dùng, chức danh (role), quyền hạn (permission) và cấp phát phiên làm việc (JWT). Nó là nguồn dữ liệu gốc cho danh tính người dùng.

### 2. Trách nhiệm

- Đăng ký và đăng nhập người dùng.
- Quản lý kiểm soát truy cập dựa trên vai trò (RBAC).
- Mã hóa và xác thực mật khẩu.
- Tiêu thụ các sự kiện liên quan đến người dùng (ví dụ: cập nhật profile qua sự kiện).

### 3. Tổng quan kiến trúc

Một ứng dụng Spring Boot sử dụng Spring Security và JPA (MySQL). Nó giao tiếp với các dịch vụ khác thông qua RabbitMQ.

### 4. Các API chính

- `/auth/login`: Xác thực và trả về JWT.
- `/auth/register`: Tạo tài khoản mới.
- `/auth/users/me`: Lấy thông tin người dùng hiện tại.
- `/auth/roles`, `/auth/permissions`: Quản lý RBAC.

### 5. Messaging / Events

- **Consumer**: Lắng nghe `auth.user.avatar.queue` để cập nhật URL avatar của người dùng khi có ảnh được upload.

### 6. Cấu hình

- `server.port`: 8182
- `spring.datasource.url`: Chuỗi kết nối MySQL.
- `jwt.secret`: Secret dùng để ký token.

### 7. Cách chạy local

Yêu cầu MySQL và RabbitMQ. Chạy class `AuthSrvApplication`.

### 8. Xử lý lỗi

Xử lý ngoại lệ tập trung qua `GlobalExceptionHandler`, trả về `BaseResponse` chuẩn hóa.

### 9. Bảo mật

Sử dụng Spring Security với session Stateless. Xác thực các internal header nếu cần thiết.

### 10. Giới hạn & Lưu ý

ID người dùng hiện đang được xử lý dưới dạng String để tương thích giữa các hệ thống.
