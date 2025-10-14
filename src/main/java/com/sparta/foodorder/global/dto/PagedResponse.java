package com.sparta.foodorder.global.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PagedResponse<T> {

    private final List<T> data;
    private final int page;
    private final int size;
    private final boolean hasNext;

    private PagedResponse(List<T> data, int page, int size, boolean hasNext) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static <T> PagedResponse<T> success(List<T> data, int page, int size, boolean hasNext) {
        return new PagedResponse<>(data, page, size, hasNext);
    }
    
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext()
        );
    }
}