package br.gymcore.dtos;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageDto<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        boolean empty
) {

    public static <T> PageDto<T> from(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.isEmpty()
        );
    }
}
