package com.sio.backend.mapper;

import org.springframework.stereotype.Component;

import com.sio.backend.dto.PatientsDTO;
import com.sio.backend.entity.PatientSex;
import com.sio.backend.entity.Patients;

@Component
public class PatientsMapper {

    public PatientsDTO toDTO(Patients entity) {
        if (entity == null) {
            return null;
        }

        return new PatientsDTO(
                entity.getId(),
                entity.getCodiceFiscale(),
                entity.getNome(),
                entity.getCognome(),
                entity.getDataNascita(),
                entity.getSex(),
                entity.getIndirizzoVia(),
                entity.getIndirizzoCivico(),
                entity.getComune(),
                entity.getProvincia());
    }

    public Patients toEntity(PatientsDTO dto) {
        if (dto == null) {
            return null;
        }

        Patients entity = new Patients();
        entity.setId(dto.id());
        entity.setCodiceFiscale(dto.codiceFiscale() != null ? dto.codiceFiscale().toUpperCase().trim() : null);
        entity.setNome(dto.nome());
        entity.setCognome(dto.cognome());
        entity.setDataNascita(dto.dataNascita());
        entity.setSex(dto.sex() != null ? dto.sex() : PatientSex.M);
        entity.setIndirizzoVia(dto.indirizzoVia());
        entity.setIndirizzoCivico(dto.indirizzoCivico());
        entity.setComune(dto.comune());
        entity.setProvincia(dto.provincia());

        return entity;
    }
}
