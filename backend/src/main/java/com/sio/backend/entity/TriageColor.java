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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a triage color.
 * Mapped to the "triage_colors" table of the "sio" schema.
 */
@Getter
@Setter
@Entity
@Table(name = "triage_colors", schema = "sio")
@Schema(description = "Persistence entity for triage colors")
public class TriageColor {

    @Id
    @Size(max = 20)
    @NotBlank
    @Column(name = "code", nullable = false, length = 20)
    @Schema(description = "Unique color code", example = "ROSSO", maxLength = 20)
    private String code;

    @Size(max = 50)
    @NotBlank
    @Column(name = "display_name", nullable = false, length = 50)
    @Schema(description = "Display name of the color", example = "Emergenza", maxLength = 50)
    private String displayName;

    @NotNull
    @Column(name = "priority", nullable = false)
    @Schema(description = "Color priority: 1 is the most urgent", example = "1")
    private Integer priority;

    @Size(max = 7)
    @NotBlank
    @Column(name = "hex_value", nullable = false, length = 7)
    @Schema(description = "Hexadecimal value of the color", example = "#FF4E4A", maxLength = 7)
    private String hexValue;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @Schema(description = "Date and time the color was registered")
    private Instant createdAt;
}
