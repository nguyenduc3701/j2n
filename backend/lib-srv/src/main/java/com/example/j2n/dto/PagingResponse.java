package com.example.j2n.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingResponse {
    private int total;
    private int current;
    private int size;
}
