package com.sio.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Biological sex of a patient.
 * Mirrors the PostgreSQL enum type {@code patient_sex}.
 */
@Schema(description = "Biological sex of a patient (M, F)")
public enum PatientSex {

    @Schema(description = "Male")
    M,

    @Schema(description = "Female")
    F
}
