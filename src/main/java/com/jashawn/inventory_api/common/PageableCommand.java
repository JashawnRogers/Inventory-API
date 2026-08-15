package com.jashawn.inventory_api.common;

import jakarta.validation.constraints.Min;

public record PageableCommand(
        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Min(value = 1, message = "Page size must be at least 1")
        Integer size
) implements PageableRequest{
    @Override
    public Integer getPageNumber() {
        if (page == null) return 0;
        return page;
    }

    @Override
    public Integer getPageSize() {
        if (size == null) return 25;
        return size;
    }
}
