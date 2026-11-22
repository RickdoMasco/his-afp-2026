package com.sio.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sio.backend.dto.ArrivalModeDTO;
import com.sio.backend.entity.ArrivalMode;
import com.sio.backend.mapper.ArrivalModeMapper;
import com.sio.backend.repository.ArrivalModeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ArrivalModeService {

    private final ArrivalModeRepository arrivalModeRepository;
    private final ArrivalModeMapper arrivalModeMapper;

    public ArrivalModeService(ArrivalModeRepository arrivalModeRepository, ArrivalModeMapper arrivalModeMapper) {
        this.arrivalModeRepository = arrivalModeRepository;
        this.arrivalModeMapper = arrivalModeMapper;
    }

    public List<ArrivalModeDTO> getAllArrivalModes() {
        log.debug("Fetching all arrival modes from the database");
        return arrivalModeRepository.findAll().stream()
                .map(arrivalModeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ArrivalModeDTO> getAllArrivalModesPaginated(Pageable pageable) {
        log.debug("Fetching arrival modes page: {}", pageable);
        return arrivalModeRepository.findAll(pageable)
                .map(arrivalModeMapper::toDTO);
    }

    public ArrivalModeDTO getArrivalModeById(String code) {
        log.debug("Fetching arrival mode with code: {}", code);
        ArrivalMode arrivalMode = arrivalModeRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Arrival mode not found with code: " + code));
        return arrivalModeMapper.toDTO(arrivalMode);
    }

    @Transactional
    public ArrivalModeDTO createArrivalMode(ArrivalModeDTO arrivalModeDTO) {
        if (arrivalModeRepository.existsById(arrivalModeDTO.code())) {
            log.warn("Attempt to create an arrival mode with an existing code: {}", arrivalModeDTO.code());
            throw new IllegalArgumentException("Arrival mode with code: " + arrivalModeDTO.code() + " already exists");
        }

        ArrivalMode saved = arrivalModeRepository.save(arrivalModeMapper.toEntity(arrivalModeDTO));
        return arrivalModeMapper.toDTO(saved);
    }

    @Transactional
    public ArrivalModeDTO editArrivalMode(String code, ArrivalModeDTO arrivalModeDTO) {
        ArrivalMode existing = arrivalModeRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Arrival mode not found with code: " + code));

        existing.setDescription(arrivalModeDTO.description());

        return arrivalModeMapper.toDTO(arrivalModeRepository.save(existing));
    }

    @Transactional
    public void deleteArrivalMode(String code) {
        arrivalModeRepository.deleteById(code);
    }
}
