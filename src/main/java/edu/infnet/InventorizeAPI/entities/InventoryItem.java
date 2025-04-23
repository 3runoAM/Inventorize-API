package edu.infnet.InventorizeAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @NotNull
    @OneToOne
    private Product product;

    @NotNull
    @PositiveOrZero
    private int currentQuantity;

    @NotNull
    @PositiveOrZero
    private int lowStockLimit;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}