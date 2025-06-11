package edu.infnet.inventorize.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @NotBlank
    @Column(length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Email
    @NotBlank
    private String notificationEmail;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AuthUser owner;
}