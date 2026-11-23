package com.sio.backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing an emergency department admission.
 * Mapped to the "admissions" table of the "sio" schema.
 */
@Entity
@Getter
@Setter
@Table(name = "admissions", schema = "sio")
@Schema(description = "Persistence entity for admissions")
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique admission identifier", example = "1")
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @Schema(description = "Patient the admission belongs to")
    private Patients patient;

    @Size(max = 20)
    @NotNull
    @Column(name = "braccialetto", nullable = false, unique = true, length = 20)
    @Schema(description = "Unique wristband code", example = "2026-4821", maxLength = 20)
    private String braccialetto;

    @Column(name = "data_ora_ingresso", updatable = false)
    @Schema(description = "Date and time the patient entered the emergency department")
    private LocalDateTime dataOraIngresso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "stato", nullable = false, columnDefinition = "admission_status")
    @Schema(description = "Clinical state of the admission", example = "ATT")
    private AdmissionStatus stato = AdmissionStatus.ATT;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patologia_code", nullable = false)
    @Schema(description = "Pathology assigned during triage")
    private Pathologies patologia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codice_colore")
    @Schema(description = "Triage color assigned to the admission; nullable because the foreign key is ON DELETE SET NULL")
    private TriageColor codiceColore;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modalita_arrivo_code", nullable = false)
    @Schema(description = "Arrival mode of the patient")
    private ArrivalMode modalitaArrivo;

    @Column(name = "note_triage", columnDefinition = "TEXT")
    @Schema(description = "Free-text triage notes")
    private String noteTriage;

    @Column(name = "updated_at")
    @Schema(description = "Date and time of the last update")
    private LocalDateTime updatedAt;

    @Column(name = "data_ora_dimissione")
    @Schema(description = "Date and time of discharge, set when the state becomes DIM")
    private LocalDateTime dataOraDimissione;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.dataOraIngresso == null) {
            this.dataOraIngresso = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.stato == null) {
            this.stato = AdmissionStatus.ATT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
