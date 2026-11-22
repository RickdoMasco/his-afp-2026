package com.sio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data identifying and describing a triage color")
public record TriageColorDTO(
        @Schema(description = "Unique color code", example = "ROSSO", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String code,
        @Schema(description = "Display name of the color", example = "Emergenza", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String displayName,
        @Schema(description = "Color priority: 1 is the most urgent", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Integer priority,
        @Schema(description = "Hexadecimal value of the color", example = "#FF4E4A", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String hexValue) {
}
