package com.example.j2n.utils;

import com.example.j2n.dto.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PageUtil {

    private static int defaultPageSize = 10;
    private static int defaultPageNo = 0;

    @Value("${application.page.default-size:10}")
    public void setDefaultPageSize(int size) {
        PageUtil.defaultPageSize = size;
    }

    @Value("${application.page.default-no:0}")
    public void setDefaultPageNo(int no) {
        PageUtil.defaultPageNo = no;
    }

    public static PageRequest buildPageRequest(int page, int size, String sortField, String sortDirection) {
        int pageNo = Math.max(page, defaultPageNo);
        int pageSize = (size <= 0) ? defaultPageSize : size;
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
            meta.setCurrent(defaultPageNo);
            meta.setSize(defaultPageSize);
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