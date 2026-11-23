package com.sio.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sio.backend.dto.PatientsDTO;
import com.sio.backend.generics.ApiResponse;
import com.sio.backend.service.PatientsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Operations for managing patients")
public class PatientsController {

    private final PatientsService patientsService;

    public PatientsController(PatientsService patientsService) {
        this.patientsService = patientsService;
    }

    @GetMapping
    @Operation(summary = "List patients", description = "Returns all registered patients, ordered by surname and name.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient list returned")
    })
    public ApiResponse<List<PatientsDTO>> getAllPatients() {
        return ApiResponse.success(patientsService.getAllPatients());
    }

    @GetMapping("/paginated")
    @Operation(summary = "List patients paginated", description = "Returns patients with pagination and sorting support.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient page returned")
    })
    public ApiResponse<Page<PatientsDTO>> getAllPatientsPaginated(Pageable pageable) {
        return ApiResponse.success(patientsService.getAllPatientsPaginated(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a patient", description = "Returns the patient identified by its id.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ApiResponse<PatientsDTO> getPatientById(
            @Parameter(description = "Patient id", example = "1") @PathVariable Integer id) {
        return ApiResponse.success(patientsService.getPatientById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Get a patient by fiscal code", description = "Returns the patient identified by its fiscal code, case-insensitive.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ApiResponse<PatientsDTO> getPatientByFiscalCode(
            @Parameter(description = "Patient fiscal code", example = "RSSMRA80E20H501U") @RequestParam String cf) {
        return ApiResponse.success(patientsService.getPatientByFiscalCode(cf));
    }

    @PostMapping
    @Operation(summary = "Create a patient", description = "Creates a patient from its demographic data.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or fiscal code already exists")
    })
    public ApiResponse<PatientsDTO> createPatient(@Valid @RequestBody PatientsDTO patientsDTO) {
        return ApiResponse.success(patientsService.createPatient(patientsDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient", description = "Updates the patient identified by its id.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ApiResponse<PatientsDTO> editPatient(
            @Valid @RequestBody PatientsDTO patientsDTO,
            @Parameter(description = "Id of the patient to update", example = "1") @PathVariable Integer id) {
        return ApiResponse.success(patientsService.editPatient(id, patientsDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Deletes the patient identified by its id.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient deleted")
    })
    public ApiResponse<Void> deletePatient(
            @Parameter(description = "Id of the patient to delete", example = "1") @PathVariable Integer id) {
        patientsService.deletePatient(id);
        return ApiResponse.success(null);
    }
}
