package com.sio.backend.mapper;

import org.springframework.stereotype.Component;

import com.sio.backend.dto.AdmissionResponseDTO;
import com.sio.backend.entity.Admission;

@Component
public class AdmissionMapper {

    private final PatientsMapper patientsMapper;
    private final PathologiesMapper pathologiesMapper;
    private final TriageColorMapper triageColorMapper;
    private final ArrivalModeMapper arrivalModeMapper;

    public AdmissionMapper(
            PatientsMapper patientsMapper,
            PathologiesMapper pathologiesMapper,
            TriageColorMapper triageColorMapper,
            ArrivalModeMapper arrivalModeMapper) {
        this.patientsMapper = patientsMapper;
        this.pathologiesMapper = pathologiesMapper;
        this.triageColorMapper = triageColorMapper;
        this.arrivalModeMapper = arrivalModeMapper;
    }

    public AdmissionResponseDTO toDTO(Admission entity) {
        if (entity == null) {
            return null;
        }

        return new AdmissionResponseDTO(
                entity.getId(),
                entity.getBraccialetto(),
                entity.getDataOraIngresso(),
                entity.getStato(),
                entity.getNoteTriage(),
                entity.getUpdatedAt(),
                entity.getDataOraDimissione(),
                patientsMapper.toDTO(entity.getPatient()),
                pathologiesMapper.toDTO(entity.getPatologia()),
                triageColorMapper.toDTO(entity.getCodiceColore()),
                arrivalModeMapper.toDTO(entity.getModalitaArrivo()));
    }
}
