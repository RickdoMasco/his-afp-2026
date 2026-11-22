package com.sio.backend.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data identifying and describing a pathology")
public record PathologiesDTO(
        @Schema(description = "Unique pathology code", example = "C02", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "C[0-9]{2,}+$")
        @NotBlank(message = "Code is required") String code,
        @Schema(description = "Pathology description", example = "Cardiologica", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String description) {
}
