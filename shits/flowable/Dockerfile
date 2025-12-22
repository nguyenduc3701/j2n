FROM flowable/flowable-ui:6.8.0
# Chuyển sang quyền root để thao tác file
USER root
# 1. Xóa thư viện cũ bị lỗi (bản 5.7.5)
RUN rm /app/WEB-INF/lib/spring-security-oauth2-client-5.7.5.jar
# 2. Tải thư viện mới (bản 5.8.1) trực tiếp từ Maven Central để đồng bộ với các thư viện Security khác
ADD https://repo1.maven.org/maven2/org/springframework/security/spring-security-oauth2-client/5.8.1/spring-security-oauth2-client-5.8.1.jar /app/WEB-INF/lib/
# 3. Phân quyền lại cho user flowable (mặc định của image gốc)
RUN chown flowable:flowable /app/WEB-INF/lib/spring-security-oauth2-client-5.8.1.jar
# Trả lại quyền user thấp để bảo mật
USER flowable