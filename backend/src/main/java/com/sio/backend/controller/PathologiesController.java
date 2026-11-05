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
import com.sio.backend.exception.GlobalExceptionHandler;
import com.sio.backend.service.PathologiesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pathologies")
@Tag(name = "Patologie", description = "Operazioni per la gestione delle patologie")
public class PathologiesController {

    private PathologiesService pathologiesService;

    public PathologiesController(PathologiesService pathologiesService) {
        this.pathologiesService = pathologiesService;
    }

    @GetMapping
    @Operation(summary = "Elenca le patologie", description = "Restituisce tutte le patologie registrate.")
    @ApiResponse(responseCode = "200", description = "Elenco delle patologie restituito")
    public List<PathologiesDTO> getAllPathologies() {
        return pathologiesService.getAllPathologies();
    }

    @PostMapping
    @Operation(summary = "Crea una patologia", description = "Crea una patologia usando codice e descrizione.")
    @ApiResponse(responseCode = "200", description = "Patologia creata")
    @ApiResponse(responseCode = "400", description = "Richiesta non valida: campi obbligatori mancanti o body malformato", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiError.class)))
    public PathologiesDTO createPathology(@Valid @RequestBody PathologiesDTO pathologyDTO) {
        return pathologiesService.createPathology(pathologyDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una patologia", description = "Elimina la patologia identificata dall'ID.")
    @ApiResponse(responseCode = "204", description = "Patologia eliminata")
    public void deletePathology(
            @Parameter(description = "ID della patologia da eliminare", example = "P001") @PathVariable String id,
            HttpServletResponse response) {
        pathologiesService.deletePathology(id);
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna una patologia", description = "Aggiorna codice e descrizione della patologia identificata dall'ID.")
    @ApiResponse(responseCode = "200", description = "Patologia aggiornata")
    @ApiResponse(responseCode = "400", description = "Richiesta non valida: campi obbligatori mancanti o body malformato", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Patologia non trovata", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ApiError.class)))
    public String putPathologies(
            @Valid @RequestBody PathologiesDTO pathologyDTO,
            @Parameter(description = "ID della patologia da aggiornare", example = "P001") @PathVariable String id) {
        pathologiesService.editPathology(id, pathologyDTO);
        return "Pathology updated successfully.";
    }

}
