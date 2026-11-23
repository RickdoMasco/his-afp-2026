package com.sio.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sio.backend.dto.AdmissionRequestDTO;
import com.sio.backend.dto.AdmissionResponseDTO;
import com.sio.backend.entity.AdmissionStatus;
import com.sio.backend.generics.ApiResponse;
import com.sio.backend.service.AdmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admissions")
@Tag(name = "Admissions", description = "Operations for managing emergency department admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @GetMapping
    @Operation(summary = "List active admissions", description = "Returns the admissions still open (state other than DIM), ordered by triage priority and arrival time.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admission list returned")
    })
    public ApiResponse<List<AdmissionResponseDTO>> getActiveAdmissions() {
        return ApiResponse.success(admissionService.getActiveAdmissions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an admission", description = "Returns the admission identified by its id, with patient and dictionaries resolved.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admission found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Admission not found")
    })
    public ApiResponse<AdmissionResponseDTO> getAdmissionById(
            @Parameter(description = "Admission id", example = "1") @PathVariable Integer id) {
        return ApiResponse.success(admissionService.getAdmissionById(id));
    }

    @GetMapping("/braccialetto/{code}")
    @Operation(summary = "Get an admission by wristband", description = "Returns the admission identified by its wristband code, case-insensitive.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admission found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Admission not found")
    })
    public ApiResponse<AdmissionResponseDTO> getAdmissionByBraccialetto(
            @Parameter(description = "Wristband code", example = "2026-0001") @PathVariable String code) {
        return ApiResponse.success(admissionService.getAdmissionByBraccialetto(code));
    }

    @PostMapping
    @Operation(summary = "Accept a patient", description = "Creates or updates the patient record, then opens an admission with state ATT. Rejects a patient that already has an open admission.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admission created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or the patient already has an open admission"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "A dictionary code was not found")
    })
    public ApiResponse<AdmissionResponseDTO> accettaPaziente(@Valid @RequestBody AdmissionRequestDTO request) {
        return ApiResponse.success(admissionService.accettaPaziente(request));
    }

    @PatchMapping("/{id}/stato")
    @Operation(summary = "Change the admission state", description = "Updates the clinical state. Setting DIM stamps the discharge timestamp once.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admission updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Admission not found")
    })
    public ApiResponse<AdmissionResponseDTO> updateStato(
            @Parameter(description = "Admission id", example = "1") @PathVariable Integer id,
            @Parameter(description = "New clinical state", example = "VIS") @RequestParam AdmissionStatus stato) {
        return ApiResponse.success(admissionService.updateStato(id, stato));
    }
}
