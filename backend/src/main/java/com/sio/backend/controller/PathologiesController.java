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
import org.springframework.web.bind.annotation.RestController;

import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.generics.ApiResponse;
import com.sio.backend.service.PathologiesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pathologies")
@Tag(name = "Pathologies", description = "Operations for managing pathologies")
public class PathologiesController {

    private final PathologiesService pathologiesService;

    public PathologiesController(PathologiesService pathologiesService) {
        this.pathologiesService = pathologiesService;
    }

    @GetMapping
    @Operation(summary = "List pathologies", description = "Returns all registered pathologies.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology list returned")
    })
    public ApiResponse<List<PathologiesDTO>> getAllPathologies() {
        return ApiResponse.success(pathologiesService.getAllPathologies());
    }

    @GetMapping("/paginated")
    @Operation(summary = "List pathologies paginated", description = "Returns pathologies with pagination and sorting support.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology page returned")
    })
    public ApiResponse<Page<PathologiesDTO>> getAllPathologiesPaginated(Pageable pageable) {
        return ApiResponse.success(pathologiesService.getAllPathologiesPaginated(pageable));
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get a pathology", description = "Returns the pathology identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pathology not found")
    })
    public ApiResponse<PathologiesDTO> getPathologyByCode(
            @Parameter(description = "Pathology code", example = "C02") @PathVariable String code) {
        return ApiResponse.success(pathologiesService.getPathologyById(code));
    }

    @PostMapping
    @Operation(summary = "Create a pathology", description = "Creates a pathology from code and description.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or code already exists")
    })
    public ApiResponse<PathologiesDTO> createPathology(@Valid @RequestBody PathologiesDTO pathologyDTO) {
        return ApiResponse.success(pathologiesService.createPathology(pathologyDTO));
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete a pathology", description = "Deletes the pathology identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology deleted")
    })
    public ApiResponse<Void> deletePathology(
            @Parameter(description = "Code of the pathology to delete", example = "C02") @PathVariable String code) {
        pathologiesService.deletePathology(code);
        return ApiResponse.success(null);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a pathology", description = "Updates code and description of the pathology identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pathology not found")
    })
    public ApiResponse<PathologiesDTO> editPathology(
            @Valid @RequestBody PathologiesDTO pathologyDTO,
            @Parameter(description = "Code of the pathology to update", example = "C02") @PathVariable String code) {
        pathologiesService.editPathology(code, pathologyDTO);
        return ApiResponse.success(pathologiesService.getPathologyById(code));
    }
}
