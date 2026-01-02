package com.example.j2n.utils;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.MessageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ResponseFactory {
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(MessageEnum.SUCCESS.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(MessageEnum.SUCCESS.getCode(), MessageEnum.SUCCESS.getMessage()));
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> of(MessageEnum msg, T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(msg.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(msg.getCode(), msg.getMessage()));
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> error(MessageEnum msg) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(String.valueOf(msg.getHttpStatus().getCode()));
        res.setMessage(combinedMessage(msg.getCode(), msg.getMessage()));
        return res;
    }

    private static String combinedMessage(String code, String message){
        return String.format("[%s] %s", code, message);
    }
}
