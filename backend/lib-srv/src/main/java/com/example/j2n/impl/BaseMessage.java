package com.example.j2n.impl;

import com.example.j2n.enums.HttpStatusCode;

public interface BaseMessage {
    String getCode();

    String getMessage();

    HttpStatusCode getHttpStatus();

    BaseMessage withArgs(Object... args);
}
