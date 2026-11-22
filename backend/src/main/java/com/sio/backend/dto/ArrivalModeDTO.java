package com.sio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data identifying and describing an arrival mode")
public record ArrivalModeDTO(
        @Schema(description = "Unique arrival mode code", example = "AMB", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String code,
        @Schema(description = "Arrival mode description", example = "Ambulanza 118", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String description) {
}
