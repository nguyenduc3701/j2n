package com.example.j2n.exception;

import com.example.j2n.impl.BaseMessage;

public class ConfigurationException extends BaseServiceException {
    public ConfigurationException(BaseMessage message) {
        super(message);
    }

    public ConfigurationException(BaseMessage message, Throwable cause) {
        super(message, cause);
    }
}
