package com.sio.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sio.backend.dto.TriageColorDTO;
import com.sio.backend.entity.TriageColor;
import com.sio.backend.mapper.TriageColorMapper;
import com.sio.backend.repository.TriageColorRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TriageColorService {

    private final TriageColorRepository triageColorRepository;
    private final TriageColorMapper triageColorMapper;

    public TriageColorService(TriageColorRepository triageColorRepository, TriageColorMapper triageColorMapper) {
        this.triageColorRepository = triageColorRepository;
        this.triageColorMapper = triageColorMapper;
    }

    public List<TriageColorDTO> getAllTriageColors() {
        log.debug("Fetching all triage colors from the database");
        return triageColorRepository.findAll().stream()
                .map(triageColorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<TriageColorDTO> getAllTriageColorsPaginated(Pageable pageable) {
        log.debug("Fetching triage colors page: {}", pageable);
        return triageColorRepository.findAll(pageable)
                .map(triageColorMapper::toDTO);
    }

    public TriageColorDTO getTriageColorById(String code) {
        log.debug("Fetching triage color with code: {}", code);
        TriageColor triageColor = triageColorRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Triage color not found with code: " + code));
        return triageColorMapper.toDTO(triageColor);
    }

    @Transactional
    public TriageColorDTO createTriageColor(TriageColorDTO triageColorDTO) {
        if (triageColorRepository.existsById(triageColorDTO.code())) {
            log.warn("Attempt to create a triage color with an existing code: {}", triageColorDTO.code());
            throw new IllegalArgumentException("Triage color with code: " + triageColorDTO.code() + " already exists");
        }

        TriageColor saved = triageColorRepository.save(triageColorMapper.toEntity(triageColorDTO));
        return triageColorMapper.toDTO(saved);
    }

    @Transactional
    public TriageColorDTO editTriageColor(String code, TriageColorDTO triageColorDTO) {
        TriageColor existing = triageColorRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Triage color not found with code: " + code));

        existing.setDisplayName(triageColorDTO.displayName());
        existing.setPriority(triageColorDTO.priority());
        existing.setHexValue(triageColorDTO.hexValue());

        return triageColorMapper.toDTO(triageColorRepository.save(existing));
    }

    @Transactional
    public void deleteTriageColor(String code) {
        triageColorRepository.deleteById(code);
    }

    /**
     * Returns the TriageColor entity for the given code, normalized to uppercase.
     * Used by other services that need the JPA entity rather than the DTO.
     *
     * @throws EntityNotFoundException if no entity exists with that code
     */
    public TriageColor getEntityByCode(String code) {
        return triageColorRepository.findById(code.toUpperCase().trim())
                .orElseThrow(() -> new EntityNotFoundException("Triage color not found with code: " + code));
    }
}
