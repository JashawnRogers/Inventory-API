package com.jashawn.inventory_api.stockMovement.web;

import com.jashawn.inventory_api.stockMovement.StockMovementService;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movement")
@Tag(name = "Stock Movements", description = "Retrieve stock movement audit records.")
@ApiResponses({
        @ApiResponse(responseCode = "404", description = "Stock movement was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class StockMovementController {

    private final StockMovementService service;
    private final StockMovementResponseAssembler assembler;

    public StockMovementController(StockMovementService service, StockMovementResponseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a stock movement by ID",
            description = "Returns one stock movement audit record with product, warehouse, employee, and department context when present.")
    @ApiResponse(responseCode = "200", description = "Stock movement found.")
    public ResponseEntity<EntityModel<StockMovementResponse>> findStockMovementById(
            @Parameter(description = "Stock movement UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findStockMovementById(id)));
    }
}
