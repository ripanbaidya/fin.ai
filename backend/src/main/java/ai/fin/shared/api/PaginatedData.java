package ai.fin.shared.api;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedData<T>(
        List<T> items,
        PaginationMeta pagination
) {
    public static <T> PaginatedData<T> from(Page<T> page) {
        return new PaginatedData<>(
                page.getContent(),
                new PaginationMeta(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast(),
                        page.hasNext(),
                        page.hasPrevious()
                )
        );
    }
}