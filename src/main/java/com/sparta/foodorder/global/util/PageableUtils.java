package com.sparta.foodorder.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

public class PageableUtils {

    private static final List<Integer> ALLOWED_PAGE_SIZES = Arrays.asList(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    public static Pageable validateAndAdjust(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        if (!ALLOWED_PAGE_SIZES.contains(pageSize)) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            sort = Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}

