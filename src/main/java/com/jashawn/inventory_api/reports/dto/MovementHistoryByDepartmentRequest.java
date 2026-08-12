package com.jashawn.inventory_api.reports.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovementHistoryByDepartmentRequest(
        @NotNull(message = "Department ID must not be null")
        UUID departmentId,

        @NotNull(message = "Start date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,

        Integer page,

        Integer size
) {
        public MovementHistoryByDepartmentRequest {
                departmentId = departmentId();
                startDate = startDate();
                endDate = endDate();
                if (page == null) page = 0;
                if (size == null) size = 25;
        }

        public PageRequest toPageRequest() {
                return PageRequest.of(this.page, this.size);
        }
}
