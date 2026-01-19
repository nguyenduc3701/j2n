package com.example.j2n.utils;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.j2n.impl.BaseMessage;

@Slf4j
@AllArgsConstructor
public class ResponseFactory {
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(BaseMessageEnum.SUCCESS.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(BaseMessageEnum.SUCCESS.getCode(), BaseMessageEnum.SUCCESS.getMessage()));
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> of(BaseMessage msg, T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(msg.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(msg.getCode(), msg.getMessage()));
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> error(BaseMessage msg) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(msg.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(msg.getCode(), msg.getMessage()));
        return res;
    }

    public static <T> BaseResponse<T> base(BaseMessage msg) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(msg.getHttpStatus().getCode()));
        res.setMessage(msg.getMessage());
        return res;
    }

    private static String combinedMessage(String code, String message) {
        return String.format("[%s] %s", code, message);
    }
}
