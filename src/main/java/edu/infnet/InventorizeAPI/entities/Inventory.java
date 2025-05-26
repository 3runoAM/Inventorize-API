package edu.infnet.InventorizeAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
public class Inventory extends AuditableEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @NotBlank
    @Column(length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<@Valid InventoryItem> inventoryItems;

    @Email
    @NotBlank
    private String notificationEmail;

    @ManyToOne
    private AuthUser owner;
}