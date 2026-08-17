package com.jashawn.inventory_api.common;

import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

public record PageableCommand(
        @Schema(description = "Zero-based page index. Defaults to 0 when omitted.", example = "0")
        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Schema(description = "Number of records per page. Defaults to 25 when omitted.", example = "25")
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
