package com.sio.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sio.backend.entity.PatientSex;
import com.sio.backend.entity.Patients;

@Repository
public interface PatientsRepository extends JpaRepository<Patients, Integer> {

    Optional<Patients> findByCodiceFiscaleIgnoreCase(String codiceFiscale);

    List<Patients> findAllByOrderByCognomeAscNomeAsc();

    List<Patients> findByNomeIgnoreCaseAndCognomeIgnoreCaseAndDataNascitaAndSex(
            String nome, String cognome, LocalDate dataNascita, PatientSex sex);
}
