package com.example.j2n.auth_srv.service.response;

import com.example.j2n.auth_srv.constant.MessageEnum;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setCode(MessageEnum.SUCCESS.getCode());
        res.setMessage(MessageEnum.SUCCESS.getMessage());
        res.setData(data);
        return res;
    }

    public static <T> BaseResponse<T> of(MessageEnum msg, T data) {
        BaseResponse<T> res = new BaseResponse<>();
        res.code = msg.getCode();
        res.message = msg.getMessage();
        res.data = data;
        return res;
    }

    public static <T> BaseResponse<T> error(MessageEnum msg) {
        BaseResponse<T> res = new BaseResponse<>();
        res.code = msg.getCode();
        res.message = msg.getMessage();
        return res;
    }
}
