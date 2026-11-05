package com.sio.backend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.entity.Pathologies;
import com.sio.backend.mapper.PathologiesMapper;
import com.sio.backend.repository.PathologiesRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    public PathologiesDTO createPathology(PathologiesDTO pathologyDTO) {
        Pathologies pathology = pathologiesMapper.toEntity(pathologyDTO);
        Pathologies savedPathology = pathologiesRepository.save(pathology);
        return pathologiesMapper.toDTO(savedPathology);
    }

    public void deletePathology(String id) {
        pathologiesRepository.deleteById(id);
    }


    public void editPathology(String id, PathologiesDTO pathologyDTO) {
        Pathologies existingPathology = pathologiesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pathology not found with id: " + id));

        existingPathology.setCode(pathologyDTO.code());
        existingPathology.setDescription(pathologyDTO.description());

        pathologiesRepository.save(existingPathology);
    }
}
