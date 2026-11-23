package com.sio.backend.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sio.backend.dto.AdmissionRequestDTO;
import com.sio.backend.dto.AdmissionResponseDTO;
import com.sio.backend.dto.PatientsDTO;
import com.sio.backend.entity.Admission;
import com.sio.backend.entity.AdmissionStatus;
import com.sio.backend.entity.ArrivalMode;
import com.sio.backend.entity.Pathologies;
import com.sio.backend.entity.Patients;
import com.sio.backend.entity.TriageColor;
import com.sio.backend.mapper.AdmissionMapper;
import com.sio.backend.mapper.PatientsMapper;
import com.sio.backend.repository.AdmissionRepository;
import com.sio.backend.repository.ArrivalModeRepository;
import com.sio.backend.repository.PathologiesRepository;
import com.sio.backend.repository.PatientsRepository;
import com.sio.backend.repository.TriageColorRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientsRepository patientsRepository;
    private final PathologiesRepository pathologiesRepository;
    private final TriageColorRepository triageColorRepository;
    private final ArrivalModeRepository arrivalModeRepository;
    private final PatientsMapper patientsMapper;
    private final AdmissionMapper admissionMapper;

    public AdmissionService(
            AdmissionRepository admissionRepository,
            PatientsRepository patientsRepository,
            PathologiesRepository pathologiesRepository,
            TriageColorRepository triageColorRepository,
            ArrivalModeRepository arrivalModeRepository,
            PatientsMapper patientsMapper,
            AdmissionMapper admissionMapper) {
        this.admissionRepository = admissionRepository;
        this.patientsRepository = patientsRepository;
        this.pathologiesRepository = pathologiesRepository;
        this.triageColorRepository = triageColorRepository;
        this.arrivalModeRepository = arrivalModeRepository;
        this.patientsMapper = patientsMapper;
        this.admissionMapper = admissionMapper;
    }

    public List<AdmissionResponseDTO> getActiveAdmissions() {
        log.debug("Fetching active admissions from the database");
        return admissionRepository
                .findByStatoNotOrderByCodiceColorePriorityAscDataOraIngressoAsc(AdmissionStatus.DIM)
                .stream()
                .map(admissionMapper::toDTO)
                .toList();
    }

    public AdmissionResponseDTO getAdmissionById(Integer id) {
        log.debug("Fetching admission with id: {}", id);
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admission not found with id: " + id));
        return admissionMapper.toDTO(admission);
    }

    public AdmissionResponseDTO getAdmissionByBraccialetto(String braccialetto) {
        log.debug("Fetching admission with wristband: {}", braccialetto);
        Admission admission = admissionRepository.findByBraccialettoIgnoreCase(braccialetto.trim())
                .orElseThrow(() -> new EntityNotFoundException("Admission not found with wristband: " + braccialetto));
        return admissionMapper.toDTO(admission);
    }

    @Transactional
    public AdmissionResponseDTO accettaPaziente(AdmissionRequestDTO request) {
        Patients patient = upsertPatient(request.patient());

        if (admissionRepository.existsByPatientIdAndStatoNot(patient.getId(), AdmissionStatus.DIM)) {
            log.warn("Patient {} already has an open admission", patient.getCodiceFiscale());
            throw new IllegalArgumentException(
                    "Patient (" + patient.getCodiceFiscale() + ") already has an open admission");
        }

        Pathologies patologia = pathologiesRepository.findById(request.patologiaCode().toUpperCase().trim())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pathology not found with code: " + request.patologiaCode()));

        TriageColor codiceColore = triageColorRepository.findById(request.codiceColore().toUpperCase().trim())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Triage color not found with code: " + request.codiceColore()));

        ArrivalMode modalitaArrivo = arrivalModeRepository.findById(request.modalitaArrivoCode().toUpperCase().trim())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Arrival mode not found with code: " + request.modalitaArrivoCode()));

        Admission admission = new Admission();
        admission.setPatient(patient);
        admission.setBraccialetto(generaBraccialetto());
        admission.setStato(AdmissionStatus.ATT);
        admission.setPatologia(patologia);
        admission.setCodiceColore(codiceColore);
        admission.setModalitaArrivo(modalitaArrivo);
        admission.setNoteTriage(request.noteTriage());

        Admission saved = admissionRepository.save(admission);
        log.info("Admission {} created for patient {}", saved.getBraccialetto(), patient.getCodiceFiscale());
        return admissionMapper.toDTO(saved);
    }

    @Transactional
    public AdmissionResponseDTO updateStato(Integer id, AdmissionStatus nuovoStato) {
        Admission admission = admissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admission not found with id: " + id));

        admission.setStato(nuovoStato);

        if (nuovoStato == AdmissionStatus.DIM && admission.getDataOraDimissione() == null) {
            admission.setDataOraDimissione(LocalDateTime.now());
        }

        return admissionMapper.toDTO(admission);
    }

    private Patients upsertPatient(PatientsDTO dto) {
        String codiceFiscale = dto.codiceFiscale().toUpperCase().trim();

        return patientsRepository.findByCodiceFiscaleIgnoreCase(codiceFiscale)
                .map(existing -> {
                    existing.setNome(dto.nome());
                    existing.setCognome(dto.cognome());
                    existing.setDataNascita(dto.dataNascita());
                    existing.setSex(dto.sex());
                    existing.setIndirizzoVia(dto.indirizzoVia());
                    existing.setIndirizzoCivico(dto.indirizzoCivico());
                    existing.setComune(dto.comune());
                    existing.setProvincia(dto.provincia());
                    return existing;
                })
                .orElseGet(() -> patientsRepository.save(patientsMapper.toEntity(dto)));
    }

    private String generaBraccialetto() {
        String code;
        do {
            code = Year.now().getValue() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (admissionRepository.findByBraccialettoIgnoreCase(code).isPresent());
        return code;
    }
}
