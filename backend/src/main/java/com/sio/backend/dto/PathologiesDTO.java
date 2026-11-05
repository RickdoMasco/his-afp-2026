package com.sio.backend.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dati identificativi e descrittivi di una patologia")
public record PathologiesDTO(
        @Schema(description = "Codice univoco della patologia", example = "P001")
        @NotBlank String code,
        @Schema(description = "Descrizione della patologia", example = "Patologia di esempio")
        @NotBlank String description) {
}
