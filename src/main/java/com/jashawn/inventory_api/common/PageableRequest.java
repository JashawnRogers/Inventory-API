package com.jashawn.inventory_api.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PageableRequest {
    Integer getPageNumber();
    Integer getPageSize();

    default Pageable toPageable() {
        return PageRequest.of(getPageNumber(), getPageSize());
    }
}
