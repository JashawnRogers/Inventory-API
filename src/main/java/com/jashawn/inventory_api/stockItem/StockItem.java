package com.jashawn.inventory_api.stockItem;

import com.jashawn.inventory_api.Exceptions.*;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A StockItem represents the current stock balance for one product in one warehouse.
 * It connects a product and warehouse while also storing additional data:
 *  - quantityOnHand
 *  - reservedQuantity
 *  - version
 *  You should not have two stock balance records for the same product and warehouse.
 */

@Getter
@Entity
@Table(
        name = "stock_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stock_item_product_warehouse",
                        columnNames = {"product_id", "warehouse_id"}
                )
        }
)
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    protected StockItem() {}

    private StockItem(Product product, Warehouse warehouse, int quantityOnHand, int reservedQuantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.quantityOnHand = quantityOnHand;
        this.reservedQuantity = reservedQuantity;
    }

    public static StockItem create(Product product, Warehouse warehouse, int quantityOnHand, int reservedQuantity) {
        if (product == null) {
            throw new InvalidFieldException("null", "StockItem", "product");
        }

        if (warehouse == null) {
            throw new InvalidFieldException("null", "StockItem", "warehouse");
        }

        if (quantityOnHand < 0) {
            throw new InvalidFieldException(String.valueOf(quantityOnHand), "StockItem", "quantityOnHand");
        }

        if (reservedQuantity < 0) {
            throw new InvalidFieldException(String.valueOf(reservedQuantity), "StockItem", "reservedQuantity");
        }

        if (reservedQuantity > quantityOnHand) {
            throw new InvalidFieldException(reservedQuantity + " > quantityOnHand", "StockItem", "reservedQuantity");
        }

        if (!product.isActive()) {
            throw new InvalidStateException("Stock item", "product", "inactive");
        }

        if (!warehouse.isActive()) {
            throw new InvalidStateException("Stock item", "warehouse", "inactive");
        }

        return new StockItem(product, warehouse, quantityOnHand, reservedQuantity);
    }

    /**
     * Removes inventory from warehouse because it is being consumed or used.
     */
    public void issue(int quantity) {
        validatePositiveQuantity(quantity);

        if (quantity > getAvailableQuantity()) {
            throw new InsufficientAvailableStockException(getId(), quantity, getAvailableQuantity());
        }

        this.quantityOnHand -= quantity;
    }

    public void receive(int quantity) {
        validatePositiveQuantity(quantity);
        this.quantityOnHand += quantity;
    }

    public void reserve(int quantity) {
        validatePositiveQuantity(quantity);

        if (quantity > getAvailableQuantity()) {
            throw new InsufficientAvailableStockException(getId(), quantity, getAvailableQuantity());
        }

        this.reservedQuantity += quantity;
    }

    public void releaseReservation(int quantity) {
        validatePositiveQuantity(quantity);

        if (quantity > getReservedQuantity()) {
            throw new InvalidStockMovementException(
                    "release stock reservation", "requested quantity exceeds reserved quantity"
            );
        }

        this.reservedQuantity -= quantity;
    }

    public void increaseByAdjustment(int quantity) {
        validatePositiveQuantity(quantity);
        this.quantityOnHand += quantity;
    }

    public void decreaseByAdjustment(int quantity) {
        validatePositiveQuantity(quantity);

        if (quantity > getAvailableQuantity()) {
            throw new BusinessRuleViolationException(
                    "Stock adjustment operation", "requested quantity", "greater than available quantity"
            );
        }

        this.quantityOnHand -= quantity;
    }

    public int getAvailableQuantity() {
        return getQuantityOnHand() - getReservedQuantity();
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Stock issue operation", "quantity", "less than or equal to 0");
        }
    }

    @PrePersist
    private void initialize() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();

    }

}
