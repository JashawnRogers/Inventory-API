package com.jashawn.inventory_api.employee.dto;

import java.util.UUID;

public record UpdateEmployeeRequest(String firstName,
                                    String lastName,
                                    String email,
                                    UUID departmentId,
                                    Boolean active) {
}
