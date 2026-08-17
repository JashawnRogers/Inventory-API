package com.jashawn.inventory_api.reports.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jashawn.inventory_api.common.PageableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DateRangeReportRequest(

        @NotNull(message = "Start date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Inclusive report start date and time.", example = "2026-08-01 00:00:00")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Inclusive report end date and time. The service expects this to be after startDate.", example = "2026-08-31 23:59:59")
        LocalDateTime endDate,

        @Schema(description = "Zero-based page index. Defaults to 0 when omitted.", example = "0")
        @Min(value = 0, message = "Page number cannot be negative")
        Integer page,

        @Schema(description = "Number of records per page. Defaults to 25 when omitted.", example = "25")
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
