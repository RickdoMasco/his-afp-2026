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
 * JPA entity representing a clinical pathology.
 * Mapped to the "pathologies" table of the "sio" schema.
 */
@Entity
@Getter
@Setter
@Table(name = "pathologies", schema = "sio")
@Schema(description = "Persistence entity for pathologies")
public class Pathologies {

    @Id
    @Size(max = 10)
    @NotBlank
    @Column(name = "code", nullable = false, length = 10)
    @Schema(description = "Unique pathology code", example = "C02", maxLength = 10)
    private String code;

    @Size(max = 100)
    @NotBlank
    @Column(name = "description", nullable = false, length = 100)
    @Schema(description = "Pathology description", example = "Cardiologica", maxLength = 100)
    private String description;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Date and time the pathology was registered")
    private Instant createdAt;
}
