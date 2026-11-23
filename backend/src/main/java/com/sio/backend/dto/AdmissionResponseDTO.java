package com.sio.backend.dto;

import java.time.LocalDateTime;

import com.sio.backend.entity.AdmissionStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data describing an admission with its patient and dictionaries resolved")
public record AdmissionResponseDTO(
        @Schema(description = "Unique admission identifier", example = "1")
        Integer id,

        @Schema(description = "Unique wristband code", example = "2026-4821")
        String braccialetto,

        @Schema(description = "Date and time the patient entered the emergency department")
        LocalDateTime dataOraIngresso,

        @Schema(description = "Clinical state of the admission", example = "ATT")
        AdmissionStatus stato,

        @Schema(description = "Free-text triage notes")
        String noteTriage,

        @Schema(description = "Date and time of the last update")
        LocalDateTime updatedAt,

        @Schema(description = "Date and time of discharge, set when the state becomes DIM")
        LocalDateTime dataOraDimissione,

        @Schema(description = "Patient the admission belongs to")
        PatientsDTO patient,

        @Schema(description = "Pathology assigned during triage")
        PathologiesDTO patologia,

        @Schema(description = "Triage color assigned to the admission")
        TriageColorDTO codiceColore,

        @Schema(description = "Arrival mode of the patient")
        ArrivalModeDTO modalitaArrivo) {
}
