package com.sio.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record PathologiesDTO(
                @NotBlank String code,
                @NotBlank String description
                ) {
}
