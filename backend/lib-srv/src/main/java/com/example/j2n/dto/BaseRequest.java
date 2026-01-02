package com.example.j2n.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseRequest {
    @JsonIgnore
    private List<String> unknownFields = new ArrayList<>();

    @JsonAnySetter
    public void addUnknownField(String key, Object value) {
        unknownFields.add(key);
    }

    public List<String> getUnknownFields() {
        return unknownFields;
    }

    public boolean hasUnknownFields() {
        return !unknownFields.isEmpty();
    }
}
