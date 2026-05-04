package com.example.j2n.payment_srv.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse<T> {
    private Integer error;
    private String message;
    private T data;

    public static <T> WebhookResponse<T> success(T data) {
        return new WebhookResponse<>(0, "success", data);
    }

    public static <T> WebhookResponse<T> success(String message, T data) {
        return new WebhookResponse<>(0, message, data);
    }

    public static <T> WebhookResponse<T> error(String message) {
        return new WebhookResponse<>(-1, message, null);
    }

    public static <T> WebhookResponse<T> error(int code, String message) {
        return new WebhookResponse<>(code, message, null);
    }
}
