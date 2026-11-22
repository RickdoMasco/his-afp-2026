package com.sio.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.entity.Pathologies;
import com.sio.backend.mapper.PathologiesMapper;
import com.sio.backend.repository.PathologiesRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PathologiesService {

    private final PathologiesRepository pathologiesRepository;
    private final PathologiesMapper pathologiesMapper;

    public PathologiesService(PathologiesRepository pathologiesRepository, PathologiesMapper pathologiesMapper) {
        this.pathologiesRepository = pathologiesRepository;
        this.pathologiesMapper = pathologiesMapper;
    }

    public List<PathologiesDTO> getAllPathologies() {
        log.debug("Fetching all pathologies from the database");    
        List<Pathologies> pathologiesList = pathologiesRepository.findAll();
        return pathologiesList.stream()
                .map(pathologiesMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<PathologiesDTO> getAllPathologiesPaginated(Pageable pageable) {
        log.debug("Fetching pathologies page: {}", pageable);
        return pathologiesRepository.findAll(pageable)
                .map(pathologiesMapper::toDTO);
    }

    public PathologiesDTO getPathologyById(String code) {
        log.debug("Fetching pathology with code: {}", code);
        Pathologies pathology = pathologiesRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Pathology not found with id: " + code));
        return pathologiesMapper.toDTO(pathology);
    }

    @Transactional
    public PathologiesDTO createPathology(PathologiesDTO pathologyDTO) {
        if (pathologiesRepository.existsById(pathologyDTO.code())) {
            log.warn("Attempt to create a pathology with an existing code: {}", pathologyDTO.code());
            throw new IllegalArgumentException("Pathology with code: " + pathologyDTO.code() + " already exists");
        }

        Pathologies pathology = pathologiesMapper.toEntity(pathologyDTO);
        Pathologies savedPathology = pathologiesRepository.save(pathology);
        return pathologiesMapper.toDTO(savedPathology);
    }

    @Transactional
    public void deletePathology(String id) {
        pathologiesRepository.deleteById(id);
    }

    @Transactional
    public void editPathology(String id, PathologiesDTO pathologyDTO) {
        Pathologies existingPathology = pathologiesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pathology not found with id: " + id));

        existingPathology.setCode(pathologyDTO.code());
        existingPathology.setDescription(pathologyDTO.description());

        pathologiesRepository.save(existingPathology);
    }
}
