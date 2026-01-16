package com.example.j2n.interfaces;

import com.example.j2n.enums.HttpStatusCode;

public interface BaseMessage {
    String getCode();

    String getMessage();

    HttpStatusCode getHttpStatus();
}
