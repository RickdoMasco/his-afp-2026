package com.sio.backend.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a patient.
 * Mapped to the "patients" table of the "sio" schema.
 */
@Entity
@Getter
@Setter
@Table(name = "patients", schema = "sio")
@Schema(description = "Persistence entity for patients")
public class Patients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Unique patient identifier", example = "1")
    private Integer id;

    @Size(max = 16)
    @NotNull
    @Column(name = "codice_fiscale", nullable = false, length = 16)
    @Schema(description = "Italian fiscal code, stored uppercase", example = "RSSMRA80E20H501U", maxLength = 16)
    private String codiceFiscale;

    @Size(max = 100)
    @NotNull
    @Column(name = "nome", nullable = false, length = 100)
    @Schema(description = "Patient first name", example = "Mario", maxLength = 100)
    private String nome;

    @Size(max = 100)
    @NotNull
    @Column(name = "cognome", nullable = false, length = 100)
    @Schema(description = "Patient surname", example = "Rossi", maxLength = 100)
    private String cognome;

    @NotNull
    @Column(name = "data_nascita", nullable = false)
    @Schema(description = "Patient date of birth", example = "1980-05-20")
    private LocalDate dataNascita;

    @ColumnDefault("'M'")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sex", columnDefinition = "patient_sex not null")
    @Schema(description = "Patient biological sex", example = "M")
    private PatientSex sex;

    @Size(max = 255)
    @Column(name = "indirizzo_via")
    @Schema(description = "Street address", example = "Via Roma", maxLength = 255)
    private String indirizzoVia;

    @Size(max = 20)
    @Column(name = "indirizzo_civico", length = 20)
    @Schema(description = "Street number", example = "10", maxLength = 20)
    private String indirizzoCivico;

    @Size(max = 100)
    @Column(name = "comune", length = 100)
    @Schema(description = "Municipality of residence", example = "Milano", maxLength = 100)
    private String comune;

    @Size(max = 5)
    @Column(name = "provincia", length = 5)
    @Schema(description = "Province code", example = "MI", maxLength = 5)
    private String provincia;

    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @Schema(description = "Date and time the patient was registered")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (codiceFiscale != null) {
            codiceFiscale = codiceFiscale.toUpperCase().trim();
        }
        if (this.sex == null) {
            this.sex = PatientSex.M;
        }
    }
}
