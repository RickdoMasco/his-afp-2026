package com.sio.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sio.backend.entity.ArrivalMode;

@Repository
public interface ArrivalModeRepository extends JpaRepository<ArrivalMode, String> {
}
