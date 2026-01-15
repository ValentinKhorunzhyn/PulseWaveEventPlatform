package com.khorunzhyn.registar.mapper;

import com.khorunzhyn.registar.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageMapper {
    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.of(
                page.getContent().stream()
                        .map(mapper)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.of(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
