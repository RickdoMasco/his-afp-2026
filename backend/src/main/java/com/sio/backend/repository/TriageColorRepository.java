package com.sio.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sio.backend.entity.TriageColor;

@Repository
public interface TriageColorRepository extends JpaRepository<TriageColor, String> {
}
