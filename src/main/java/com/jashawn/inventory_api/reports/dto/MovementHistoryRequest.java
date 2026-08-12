package com.jashawn.inventory_api.reports.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

public record MovementHistoryRequest(

        @NotNull(message = "Start date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,

        Integer size,

        Integer page

) {
        public MovementHistoryRequest {
            startDate = startDate();
            endDate = endDate();
            if (size == null) size = 0;
            if (page == null) page = 25;
        }

        public PageRequest toPageRequest() {
            return PageRequest.of(this.page, this.size);
        }
}
