package edu.infnet.InventorizeAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@ToString(exclude = {"product", "inventory"})
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem extends AuditableEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @NotNull
    @PositiveOrZero
    private int currentQuantity;

    @NotNull
    @PositiveOrZero
    private int lowStockLimit;
}