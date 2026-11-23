package com.sio.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sio.backend.dto.PatientsDTO;
import com.sio.backend.entity.Patients;
import com.sio.backend.mapper.PatientsMapper;
import com.sio.backend.repository.PatientsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PatientsService {

    private final PatientsRepository patientsRepository;
    private final PatientsMapper patientsMapper;

    public PatientsService(PatientsRepository patientsRepository, PatientsMapper patientsMapper) {
        this.patientsRepository = patientsRepository;
        this.patientsMapper = patientsMapper;
    }

    public List<PatientsDTO> getAllPatients() {
        log.debug("Fetching all patients from the database");
        return patientsRepository.findAllByOrderByCognomeAscNomeAsc().stream()
                .map(patientsMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<PatientsDTO> getAllPatientsPaginated(Pageable pageable) {
        log.debug("Fetching patients page: {}", pageable);
        return patientsRepository.findAll(pageable)
                .map(patientsMapper::toDTO);
    }

    public PatientsDTO getPatientById(Integer id) {
        log.debug("Fetching patient with id: {}", id);
        Patients patient = patientsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));
        return patientsMapper.toDTO(patient);
    }

    public PatientsDTO getPatientByFiscalCode(String codiceFiscale) {
        log.debug("Fetching patient with fiscal code: {}", codiceFiscale);
        Patients patient = patientsRepository.findByCodiceFiscaleIgnoreCase(codiceFiscale.trim())
                .orElseThrow(() -> new NoSuchElementException("Patient not found with fiscal code: " + codiceFiscale));
        return patientsMapper.toDTO(patient);
    }

    @Transactional
    public PatientsDTO createPatient(PatientsDTO patientsDTO) {
        if (patientsRepository.findByCodiceFiscaleIgnoreCase(patientsDTO.codiceFiscale().trim()).isPresent()) {
            log.warn("Attempt to create a patient with an existing fiscal code: {}", patientsDTO.codiceFiscale());
            throw new IllegalArgumentException(
                    "Patient with fiscal code: " + patientsDTO.codiceFiscale() + " already exists");
        }

        Patients saved = patientsRepository.save(patientsMapper.toEntity(patientsDTO));
        return patientsMapper.toDTO(saved);
    }

    @Transactional
    public PatientsDTO editPatient(Integer id, PatientsDTO patientsDTO) {
        Patients existing = patientsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patient not found with id: " + id));

        existing.setCodiceFiscale(patientsDTO.codiceFiscale().toUpperCase().trim());
        existing.setNome(patientsDTO.nome());
        existing.setCognome(patientsDTO.cognome());
        existing.setDataNascita(patientsDTO.dataNascita());
        existing.setSex(patientsDTO.sex());
        existing.setIndirizzoVia(patientsDTO.indirizzoVia());
        existing.setIndirizzoCivico(patientsDTO.indirizzoCivico());
        existing.setComune(patientsDTO.comune());
        existing.setProvincia(patientsDTO.provincia());

        return patientsMapper.toDTO(patientsRepository.save(existing));
    }

    @Transactional
    public void deletePatient(Integer id) {
        patientsRepository.deleteById(id);
    }
}
