# Image Service (image-srv)

## English Version

### 1. Title & Overview

The **Image Service** handles all media storage and retrieval. It integrates with object storage (Minio) and database (MySQL) to manage image metadata.

### 2. Responsibilities

- Uploading images to Minio.
- Retrieving images as binary streams.
- Publishing events when specific images (like avatars) are uploaded.
- Managing image ownership and types (e.g., USER, PRODUCT).

### 3. Architecture Overview

Spring Boot with JPA and Minio SDK. Uses RabbitMQ for asynchronous notifications to other services (like `auth-srv`).

### 4. API Endpoints

- `/image/upload`: Multipart upload with owner info.
- `/image/{ownerType}/{id}`: Get raw image data.
- `/image/file/curriculum-vitae/{year}`: Static file retrieval.

### 5. Messaging / Events

- **Publisher**: Publishes `UserAvatarUploadEvent` to `user.exchange` when a user uploads a new avatar.

### 6. Configuration

- `server.port`: 8183
- `minio.url`, `minio.access-key`, `minio.secret-key`: Minio connection details.
- `spring.rabbitmq.*`: RabbitMQ settings.

### 7. Run Locally

Requires MySQL, RabbitMQ, and a running Minio instance (port 9000). Run `ImageSrvApplication`.

### 8. Error Handling

Catches storage exceptions and returns appropriate API responses.

### 9. Security

Often requires internal token validation or delegated auth via Gateway.

### 10. Limitations & Notes

Maximum file size is currently configured at 50MB.

---

## Phiên bản tiếng Việt

### 1. Tiêu đề & Tổng quan

**Image Service** xử lý tất cả việc lưu trữ và truy xuất phương tiện truyền thông. Nó tích hợp với lưu trữ đối tượng (Minio) và cơ sở dữ liệu (MySQL) để quản lý metadata của hình ảnh.

### 2. Trách nhiệm

- Tải hình ảnh lên Minio.
- Truy xuất hình ảnh dưới dạng luồng dữ liệu nhị phân (binary streams).
- Phát hành các sự kiện khi các hình ảnh cụ thể (như avatar) được tải lên.
- Quản lý chủ sở hữu và loại hình ảnh (ví dụ: USER, PRODUCT).

### 3. Tổng quan kiến trúc

Spring Boot với JPA và Minio SDK. Sử dụng RabbitMQ để thông báo không đồng bộ cho các dịch vụ khác (như `auth-srv`).

### 4. Các API chính

- `/image/upload`: Upload multipart với thông tin chủ sở hữu.
- `/image/{ownerType}/{id}`: Lấy dữ liệu ảnh thô.
- `/image/file/curriculum-vitae/{year}`: Truy xuất tệp tĩnh.

### 5. Messaging / Events

- **Publisher**: Phát hành `UserAvatarUploadEvent` tới `user.exchange` khi người dùng tải lên avatar mới.

### 6. Cấu hình

- `server.port`: 8183
- `minio.url`, `minio.access-key`, `minio.secret-key`: Thông tin kết nối Minio.
- `spring.rabbitmq.*`: Cấu hình RabbitMQ.

### 7. Cách chạy local

Yêu cầu MySQL, RabbitMQ và một thực thể Minio đang chạy (cổng 9000). Chạy class `ImageSrvApplication`.

### 8. Xử lý lỗi

Bắt các ngoại lệ lưu trữ và trả về phản hồi API thích hợp.

### 9. Bảo mật

Thường yêu cầu xác thực internal token hoặc ủy quyền xác thực qua Gateway.

### 10. Giới hạn & Lưu ý

Kích thước tệp tối đa hiện được cấu hình là 50MB.
