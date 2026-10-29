package com.sio.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.service.PathologiesService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/pathologies")
public class PathologiesController {

    private PathologiesService pathologiesService;

    public PathologiesController(PathologiesService pathologiesService) {
        this.pathologiesService = pathologiesService;
    }

    @GetMapping
    public List<PathologiesDTO> getAllPathologies() {
        return pathologiesService.getAllPathologies();
    }

    @PostMapping
    public PathologiesDTO createPathology(@Valid @RequestBody PathologiesDTO pathologyDTO) {
        return pathologiesService.createPathology(pathologyDTO);
    }

    @DeleteMapping("/{id}")
    public void deletePathology(@PathVariable String id, HttpServletResponse response) {
        pathologiesService.deletePathology(id);
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @PutMapping("/{id}")
    public String putPathologies(@Valid @RequestBody PathologiesDTO pathologyDTO, @PathVariable String id) {
        pathologiesService.editPathology(id, pathologyDTO);
        return "Pathology updated successfully.";
    }

    
}
