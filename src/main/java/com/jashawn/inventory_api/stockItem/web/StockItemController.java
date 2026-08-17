package com.jashawn.inventory_api.stockItem.web;

import com.jashawn.inventory_api.stockItem.StockItemService;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
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
@RequestMapping("/api/v1/stock-item")
@Tag(name = "Stock Items", description = "Retrieve stock item inventory records.")
@ApiResponses({
        @ApiResponse(responseCode = "404", description = "Stock item was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class StockItemController {

    private final StockItemService service;
    private final StockItemResponseAssembler assembler;

    public StockItemController(StockItemService service, StockItemResponseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a stock item by ID",
            description = "Returns one stock item, including its product and warehouse summaries.")
    @ApiResponse(responseCode = "200", description = "Stock item found.")
    public ResponseEntity<EntityModel<StockItemResponse>> findStockItem(
            @Parameter(description = "Stock item UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findStockItem(id)));
    }
}
