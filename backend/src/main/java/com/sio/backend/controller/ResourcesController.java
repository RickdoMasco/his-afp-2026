package com.sio.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sio.backend.dto.ArrivalModeDTO;
import com.sio.backend.dto.PathologiesDTO;
import com.sio.backend.dto.TriageColorDTO;
import com.sio.backend.generics.ApiResponse;
import com.sio.backend.service.ArrivalModeService;
import com.sio.backend.service.PathologiesService;
import com.sio.backend.service.TriageColorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Aggregated controller for the SIO lookup resources.
 * Exposes the dictionaries the frontend needs to populate its forms.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/resources")
@Tag(name = "SIO resources", description = "Endpoints for the SIO lookup resources")
public class ResourcesController {

    private final TriageColorService triageColorService;
    private final ArrivalModeService arrivalModeService;
    private final PathologiesService pathologiesService;

    public ResourcesController(
            TriageColorService triageColorService,
            ArrivalModeService arrivalModeService,
            PathologiesService pathologiesService) {
        this.triageColorService = triageColorService;
        this.arrivalModeService = arrivalModeService;
        this.pathologiesService = pathologiesService;
    }

    // --- Triage colors ---

    @GetMapping("/triage-colors")
    @Operation(summary = "List triage colors", description = "Returns all triage colors.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color list returned")
    })
    public ApiResponse<List<TriageColorDTO>> getTriageColors() {
        return ApiResponse.success(triageColorService.getAllTriageColors());
    }

    @GetMapping("/triage-colors/paginated")
    @Operation(summary = "List triage colors paginated", description = "Returns triage colors with pagination and sorting support.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color page returned")
    })
    public ApiResponse<Page<TriageColorDTO>> getTriageColorsPaginated(Pageable pageable) {
        return ApiResponse.success(triageColorService.getAllTriageColorsPaginated(pageable));
    }

    @GetMapping("/triage-colors/{code}")
    @Operation(summary = "Get a triage color", description = "Returns the triage color identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Triage color not found")
    })
    public ApiResponse<TriageColorDTO> getTriageColorByCode(
            @Parameter(description = "Triage color code", example = "ROSSO") @PathVariable String code) {
        return ApiResponse.success(triageColorService.getTriageColorById(code));
    }

    @PostMapping("/triage-colors")
    @Operation(summary = "Create a triage color", description = "Creates a triage color.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or code already exists")
    })
    public ApiResponse<TriageColorDTO> createTriageColor(@Valid @RequestBody TriageColorDTO triageColorDTO) {
        return ApiResponse.success(triageColorService.createTriageColor(triageColorDTO));
    }

    @PutMapping("/triage-colors/{code}")
    @Operation(summary = "Update a triage color", description = "Updates the triage color identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Triage color not found")
    })
    public ApiResponse<TriageColorDTO> editTriageColor(
            @Valid @RequestBody TriageColorDTO triageColorDTO,
            @Parameter(description = "Code of the triage color to update", example = "ROSSO") @PathVariable String code) {
        return ApiResponse.success(triageColorService.editTriageColor(code, triageColorDTO));
    }

    @DeleteMapping("/triage-colors/{code}")
    @Operation(summary = "Delete a triage color", description = "Deletes the triage color identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Triage color deleted")
    })
    public ApiResponse<Void> deleteTriageColor(
            @Parameter(description = "Code of the triage color to delete", example = "ROSSO") @PathVariable String code) {
        triageColorService.deleteTriageColor(code);
        return ApiResponse.success(null);
    }

    // --- Arrival modes ---

    @GetMapping("/arrival-modes")
    @Operation(summary = "List arrival modes", description = "Returns all arrival modes.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode list returned")
    })
    public ApiResponse<List<ArrivalModeDTO>> getArrivalModes() {
        return ApiResponse.success(arrivalModeService.getAllArrivalModes());
    }

    @GetMapping("/arrival-modes/paginated")
    @Operation(summary = "List arrival modes paginated", description = "Returns arrival modes with pagination and sorting support.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode page returned")
    })
    public ApiResponse<Page<ArrivalModeDTO>> getArrivalModesPaginated(Pageable pageable) {
        return ApiResponse.success(arrivalModeService.getAllArrivalModesPaginated(pageable));
    }

    @GetMapping("/arrival-modes/{code}")
    @Operation(summary = "Get an arrival mode", description = "Returns the arrival mode identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Arrival mode not found")
    })
    public ApiResponse<ArrivalModeDTO> getArrivalModeByCode(
            @Parameter(description = "Arrival mode code", example = "AMB") @PathVariable String code) {
        return ApiResponse.success(arrivalModeService.getArrivalModeById(code));
    }

    @PostMapping("/arrival-modes")
    @Operation(summary = "Create an arrival mode", description = "Creates an arrival mode.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or code already exists")
    })
    public ApiResponse<ArrivalModeDTO> createArrivalMode(@Valid @RequestBody ArrivalModeDTO arrivalModeDTO) {
        return ApiResponse.success(arrivalModeService.createArrivalMode(arrivalModeDTO));
    }

    @PutMapping("/arrival-modes/{code}")
    @Operation(summary = "Update an arrival mode", description = "Updates the arrival mode identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Arrival mode not found")
    })
    public ApiResponse<ArrivalModeDTO> editArrivalMode(
            @Valid @RequestBody ArrivalModeDTO arrivalModeDTO,
            @Parameter(description = "Code of the arrival mode to update", example = "AMB") @PathVariable String code) {
        return ApiResponse.success(arrivalModeService.editArrivalMode(code, arrivalModeDTO));
    }

    @DeleteMapping("/arrival-modes/{code}")
    @Operation(summary = "Delete an arrival mode", description = "Deletes the arrival mode identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arrival mode deleted")
    })
    public ApiResponse<Void> deleteArrivalMode(
            @Parameter(description = "Code of the arrival mode to delete", example = "AMB") @PathVariable String code) {
        arrivalModeService.deleteArrivalMode(code);
        return ApiResponse.success(null);
    }

    // --- Pathologies ---

    @GetMapping("/pathologies")
    @Operation(summary = "List pathologies paginated", description = "Returns pathologies with pagination and sorting support.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology page returned")
    })
    public ApiResponse<Page<PathologiesDTO>> getPathologies(Pageable pageable) {
        return ApiResponse.success(pathologiesService.getAllPathologiesPaginated(pageable));
    }

    @GetMapping("/pathologies/{code}")
    @Operation(summary = "Get a pathology", description = "Returns the pathology identified by its code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pathology not found")
    })
    public ApiResponse<PathologiesDTO> getPathologyByCode(
            @Parameter(description = "Pathology code", example = "C02") @PathVariable String code) {
        return ApiResponse.success(pathologiesService.getPathologyById(code));
    }

    @PostMapping("/pathologies")
    @Operation(summary = "Create a pathology", description = "Creates a pathology from code and description.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data or code already exists")
    })
    public ApiResponse<PathologiesDTO> createPathology(@Valid @RequestBody PathologiesDTO pathologyDTO) {
        return ApiResponse.success(pathologiesService.createPathology(pathologyDTO));
    }

    @PutMapping("/pathologies")
    @Operation(summary = "Update a pathology", description = "Updates the pathology identified by the code in the body.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pathology not found")
    })
    public ApiResponse<PathologiesDTO> editPathology(@Valid @RequestBody PathologiesDTO pathologyDTO) {
        pathologiesService.editPathology(pathologyDTO.code(), pathologyDTO);
        return ApiResponse.success(pathologiesService.getPathologyById(pathologyDTO.code()));
    }

    @DeleteMapping("/pathologies")
    @Operation(summary = "Delete a pathology", description = "Deletes the pathology identified by the code query parameter.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pathology deleted")
    })
    public ApiResponse<Void> deletePathology(
            @Parameter(description = "Code of the pathology to delete", example = "C02") @RequestParam String code) {
        pathologiesService.deletePathology(code);
        return ApiResponse.success(null);
    }

    // --- Diagnostics ---

    @GetMapping("/sim-error")
    @Operation(summary = "Simulate an error", description = "Test endpoint to verify the global exception handling.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Simulated internal server error")
    })
    public ApiResponse<PathologiesDTO> getSimError() throws Exception {
        throw new Exception("Simulated failure");
    }
}
