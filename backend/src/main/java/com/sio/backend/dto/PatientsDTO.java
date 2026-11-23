package com.sio.backend.dto;

import java.time.LocalDate;

import com.sio.backend.entity.PatientSex;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Schema(description = "Data identifying and describing a patient")
public record PatientsDTO(
        @Schema(description = "Unique patient identifier", example = "1")
        Integer id,

        @Schema(description = "Italian fiscal code", example = "RSSMRA80E20H501U", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Fiscal code is required")
        @Size(min = 16, max = 16, message = "Fiscal code must be 16 characters long")
        String codiceFiscale,

        @Schema(description = "Patient first name", example = "Mario", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String nome,

        @Schema(description = "Patient surname", example = "Rossi", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Surname is required")
        @Size(max = 100)
        String cognome,

        @Schema(description = "Patient date of birth", example = "1980-05-20", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Date of birth is required")
        @PastOrPresent(message = "Date of birth cannot be in the future")
        LocalDate dataNascita,

        @Schema(description = "Patient biological sex", example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Sex is required")
        PatientSex sex,

        @Schema(description = "Street address", example = "Via Roma", maxLength = 255)
        @Size(max = 255)
        String indirizzoVia,

        @Schema(description = "Street number", example = "10", maxLength = 20)
        @Size(max = 20)
        String indirizzoCivico,

        @Schema(description = "Municipality of residence", example = "Milano", maxLength = 100)
        @Size(max = 100)
        String comune,

        @Schema(description = "Province code", example = "MI", maxLength = 5)
        @Size(max = 5)
        String provincia) {
}
