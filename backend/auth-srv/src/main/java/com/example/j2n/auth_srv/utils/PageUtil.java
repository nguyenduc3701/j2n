package com.example.j2n.auth_srv.utils;

import com.example.j2n.auth_srv.service.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageUtil {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NO = 0;

    public static PageRequest buildPageRequest(int page, int size, String sortField, String sortDirection) {
        int pageNo = Math.max(page, DEFAULT_PAGE_NO);
        int pageSize = (size <= 0) ? DEFAULT_PAGE_SIZE : size;
        if (!StringUtils.hasText(sortField)) {
            return PageRequest.of(pageNo, pageSize);
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (StringUtils.hasText(sortDirection) && sortDirection.equalsIgnoreCase("DESC")) {
            direction = Sort.Direction.DESC;
        }
        return PageRequest.of(pageNo, pageSize, Sort.by(direction, sortField));
    }

    public static PageRequest buildPageRequest(int page, int size) {
        return buildPageRequest(page, size, null, null);
    }

    public static PagingResponse buildPagingMeta(Page<?> pageData) {
        PagingResponse meta = new PagingResponse();
        if (pageData == null) {
            meta.setTotal(0);
            meta.setCurrent(DEFAULT_PAGE_NO);
            meta.setSize(DEFAULT_PAGE_SIZE);
            return meta;
        }
        meta.setTotal((int) pageData.getTotalElements());
        meta.setCurrent(pageData.getNumber());
        meta.setSize(pageData.getSize());
        return meta;
    }

    public static <T, R> List<R> mapContent(Page<T> pageData, Function<T, R> mapper) {
        if (pageData == null || pageData.isEmpty()) {
            return Collections.emptyList();
        }
        return pageData.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}