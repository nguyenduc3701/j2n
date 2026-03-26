package com.example.j2n.config_srv.controller.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetConfigurationsRequest {
    private List<String> keys;
}
