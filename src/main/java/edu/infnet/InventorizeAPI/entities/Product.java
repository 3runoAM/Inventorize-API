package edu.infnet.InventorizeAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@ToString(exclude = "inventoryItems")
@AllArgsConstructor
@NoArgsConstructor
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @NotBlank
    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String supplierCode;

    @OneToMany(mappedBy = "product")
    private List<InventoryItem> inventoryItems;

    @ManyToOne
    private AuthUser owner;
}