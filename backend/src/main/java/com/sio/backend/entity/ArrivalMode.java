package com.sio.backend.entity;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing an arrival mode in the emergency department.
 * Mapped to the "arrival_modes" table of the "sio" schema.
 */
@Getter
@Setter
@Entity
@Table(name = "arrival_modes", schema = "sio")
@Schema(description = "Persistence entity for arrival modes")
public class ArrivalMode {

    @Id
    @Size(max = 10)
    @NotBlank
    @Column(name = "code", nullable = false, length = 10)
    @Schema(description = "Unique arrival mode code", example = "AMB", maxLength = 10)
    private String code;

    @Size(max = 50)
    @NotBlank
    @Column(name = "description", nullable = false, length = 50)
    @Schema(description = "Arrival mode description", example = "Ambulanza 118", maxLength = 50)
    private String description;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @Schema(description = "Date and time the arrival mode was registered")
    private Instant createdAt;
}
