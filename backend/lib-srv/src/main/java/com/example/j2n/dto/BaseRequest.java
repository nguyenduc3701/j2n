package com.example.j2n.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public class BaseRequest {
    @JsonIgnore
    @Schema(hidden = true)
    private List<String> unknownFields = new ArrayList<>();

    @JsonAnySetter
    @Schema(hidden = true)
    public void addUnknownField(String key, Object value) {
        unknownFields.add(key);
    }

    @Schema(hidden = true)
    public List<String> getUnknownFields() {
        return unknownFields;
    }

    @Schema(hidden = true)
    public boolean hasUnknownFields() {
        return !unknownFields.isEmpty();
    }
}
