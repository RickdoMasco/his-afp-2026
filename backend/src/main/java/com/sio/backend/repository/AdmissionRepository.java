package com.sio.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sio.backend.entity.Admission;
import com.sio.backend.entity.AdmissionStatus;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Integer> {

    boolean existsByPatientIdAndStatoNot(Integer patientId, AdmissionStatus stato);

    @EntityGraph(attributePaths = { "patient", "patologia", "codiceColore", "modalitaArrivo" })
    List<Admission> findByStatoNotOrderByCodiceColorePriorityAscDataOraIngressoAsc(AdmissionStatus stato);

    @EntityGraph(attributePaths = { "patient", "patologia", "codiceColore", "modalitaArrivo" })
    List<Admission> findAllByOrderByDataOraIngressoDesc();

    @EntityGraph(attributePaths = { "patient", "patologia", "codiceColore", "modalitaArrivo" })
    Optional<Admission> findById(Integer id);

    @EntityGraph(attributePaths = { "patient", "patologia", "codiceColore", "modalitaArrivo" })
    Optional<Admission> findByBraccialettoIgnoreCase(String braccialetto);
}
