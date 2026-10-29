package com.sio.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sio.backend.entity.Pathologies;

public interface PathologiesRepository extends JpaRepository<Pathologies, String> {
}
