package com.example.j2n.api_gateway_srv.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrpcFactory {

    private final ObjectMapper objectMapper;
    private final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    /**
     * Bọc lời gọi gRPC Blocking vào Mono Reactive và đóng gói thành ResponseEntity
     * JSON.
     */
    public Mono<ResponseEntity<String>> invokeAndWrap(Callable<String> invokerTask, String methodName) {
        return Mono.fromCallable(invokerTask)
                .map(jsonResponse -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonResponse))
                .onErrorResume(e -> {
                    log.error("[GRPC-FACTORY] Error invoking gRPC for method {}: {}", methodName, e.getMessage());
                    return Mono.error(e);
                });
    }

    /**
     * Chuyển đổi dữ liệu Multipart sang JSON String (File sẽ được mã hóa Base64)
     */
    public Mono<String> convertMultipartToJson(MultiValueMap<String, Part> partsMap) {
        ObjectNode rootNode = objectMapper.createObjectNode();

        return Flux.fromIterable(partsMap.entrySet())
                .flatMap(entry -> Flux.fromIterable(entry.getValue()).flatMap(part -> {
                    if (part instanceof FormFieldPart formField) {
                        rootNode.put(entry.getKey(), formField.value());
                    } else if (part instanceof FilePart filePart) {
                        return DataBufferUtils.join(filePart.content())
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    String base64 = Base64.getEncoder().encodeToString(bytes);
                                    rootNode.put(entry.getKey(), base64);
                                    return Mono.empty();
                                });
                    }
                    return Mono.empty();
                }))
                .then(Mono.fromCallable(rootNode::toString));
    }

    /**
     * Thực hiện lời gọi gRPC động bằng cách convert JSON body sang Protobuf Request
     * và convert Protobuf Response ngược lại thành JSON.
     *
     * @param stub       Đối tượng BlockingStub (ví dụ: ReportServiceBlockingStub)
     * @param methodName Tên method (không phân biệt hoa thường)
     * @param jsonBody   Nội dung request dạng JSON
     * @return Kết quả trả về từ gRPC dạng JSON
     */
    public String dynamicInvoke(Object stub, String methodName, String jsonBody) throws Exception {
        log.debug("[GRPC-FACTORY] Invoking method: {} on stub: {}", methodName, stub.getClass().getSimpleName());

        // 1. Tìm method trong cache hoặc qua reflection
        Method method = findMethod(stub, methodName);

        // 2. Xác định kiểu dữ liệu Request (params đầu tiên của method trong blocking
        // stub)
        Class<?> requestType = method.getParameterTypes()[0];

        // 3. Tạo Request Builder và parse JSON sang Protobuf Message
        Message.Builder builder = (Message.Builder) requestType.getMethod("newBuilder").invoke(null);
        if (jsonBody != null && !jsonBody.isEmpty() && !jsonBody.equals("{}")) {
            JsonFormat.parser().ignoringUnknownFields().merge(jsonBody, builder);
        }
        Message request = builder.build();

        // 4. Thực thi gRPC call qua stub
        Object response = method.invoke(stub, request);

        // 5. Convert Protobuf Response sang JSON String
        return JsonFormat.printer()
                .includingDefaultValueFields()
                .preservingProtoFieldNames()
                .print((Message) response);
    }

    /**
     * Tạo ManagedChannel từ một URL (base-url).
     */
    public ManagedChannel createManagedChannel(String baseUrl) throws Exception {
        URI uri = new URI(baseUrl);
        String host = uri.getHost();
        int port = uri.getPort();

        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    private Method findMethod(Object stub, String methodName) throws NoSuchMethodException {
        String cacheKey = stub.getClass().getName() + "_" + methodName.toLowerCase();
        Method method = methodCache.computeIfAbsent(cacheKey, k -> {
            for (Method m : stub.getClass().getDeclaredMethods()) {
                if (m.getName().equalsIgnoreCase(methodName)) {
                    return m;
                }
            }
            return null;
        });

        if (method == null) {
            throw new NoSuchMethodException(
                    "Method " + methodName + " not found in stub " + stub.getClass().getSimpleName());
        }
        return method;
    }
}
