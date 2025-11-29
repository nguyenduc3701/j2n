package com.example.j2n.auth_srv.service.response;

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
