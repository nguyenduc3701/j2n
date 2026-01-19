package com.example.j2n.bff_srv.dto;

import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.impl.BaseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownstreamMessage implements BaseMessage {
    private String code;
    private HttpStatusCode httpStatus;
    private String message;

    @Override
    public BaseMessage withArgs(Object... args) {
        return this;
    }
}
