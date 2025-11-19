# Stage 1: build
# Chỉ định nền tảng AMD64 để đảm bảo tìm thấy image trên các kiến trúc khác (như ARM)
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

# Build the JAR file, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: runner
# Chỉ định nền tảng AMD64 cho base image JRE Alpine
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Thiết lập biến môi trường
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8180

# Sao chép JAR đã build từ Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Cổng ứng dụng lắng nghe
EXPOSE 8180
# Lệnh chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar", "--server.port=8180"]