package com.sio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to accept a patient into the emergency department")
public record AdmissionRequestDTO(
        @Schema(description = "Demographic data of the patient; an existing record is updated", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Patient demographic data is required")
        @Valid
        PatientsDTO patient,

        @Schema(description = "Pathology code", example = "C02", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Pathology code is required")
        @Size(max = 10)
        String patologiaCode,

        @Schema(description = "Triage color code", example = "ROSSO", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Triage color code is required")
        @Size(max = 20)
        String codiceColore,

        @Schema(description = "Arrival mode code", example = "AMB", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Arrival mode code is required")
        @Size(max = 10)
        String modalitaArrivoCode,

        @Schema(description = "Free-text triage notes")
        String noteTriage) {
}
