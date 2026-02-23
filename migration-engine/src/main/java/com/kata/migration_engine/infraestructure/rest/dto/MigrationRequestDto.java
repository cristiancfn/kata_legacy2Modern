package com.kata.migration_engine.infraestructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MigrationRequestDto(
    @NotBlank(message = "El código fuente es obligatorio.")
    @Size(max = 10000, message = "Por seguridad, el código fuente no puede exceder los 10.000 caracteres.")
    String sourceCode,

    @NotBlank(message = "El lenguaje de origen es obligatorio.")
    String sourceLanguage,

    @NotBlank(message = "El lenguaje de destino es obligatorio.")
    String targetLanguage
) {

}
