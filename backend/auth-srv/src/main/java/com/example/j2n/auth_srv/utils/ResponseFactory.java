package com.example.j2n.auth_srv.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.service.response.BaseResponse;

@Slf4j
@AllArgsConstructor
public class ResponseFactory {
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(MessageEnum.SUCCESS.getCode());
        res.setMessage(MessageEnum.SUCCESS.getMessage());
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> of(MessageEnum msg, T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(msg.getCode());
        res.setMessage(msg.getMessage());
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> error(MessageEnum msg) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(msg.getCode());
        res.setMessage(msg.getMessage());
        return res;
    }
}
