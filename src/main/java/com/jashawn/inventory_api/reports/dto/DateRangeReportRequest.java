package com.jashawn.inventory_api.reports.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jashawn.inventory_api.common.PageableRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DateRangeReportRequest(

        @NotNull(message = "Start date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,

        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Min(value = 1, message = "Page size must be at least 1")
        Integer size

) implements PageableRequest {
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
