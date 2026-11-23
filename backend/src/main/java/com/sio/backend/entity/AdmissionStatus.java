package com.sio.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Clinical state of an admission.
 * Mirrors the PostgreSQL enum type {@code admission_status}.
 */
@Schema(description = "Clinical state of an admission (ATT, VIS, OBI, RIC, DIM)")
public enum AdmissionStatus {

    @Schema(description = "Waiting")
    ATT,

    @Schema(description = "Under examination")
    VIS,

    @Schema(description = "Short intensive observation")
    OBI,

    @Schema(description = "Admitted to ward")
    RIC,

    @Schema(description = "Discharged")
    DIM
}
