package com.sio.backend.mapper;

import org.springframework.stereotype.Component;

import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.entity.Pathologies;

@Component
public class PathologiesMapper {

    public PathologiesDTO toDTO(Pathologies pathologies) {
        if (pathologies == null) {
            return null;
        }

        return new PathologiesDTO(
                pathologies.getCode(),
                pathologies.getDescription());
    }

    public Pathologies toEntity(PathologiesDTO pathologiesDTO) {

        if (pathologiesDTO == null) {
            return null;
        }

        Pathologies pathologies = new Pathologies();

        pathologies.setCode(pathologiesDTO.code());
        pathologies.setDescription(pathologiesDTO.description());

        return pathologies;
    }
}
