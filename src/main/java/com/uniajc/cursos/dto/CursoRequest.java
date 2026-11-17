package com.uniajc.cursos.dto;

import jakarta.validation.constraints.*;

/** Datos que llegan para crear / actualizar un curso. */
public record CursoRequest(
        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 20, message = "El codigo tiene maximo 20 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre tiene maximo 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripcion tiene maximo 255 caracteres")
        String descripcion,

        @NotNull(message = "Los creditos son obligatorios")
        @Min(value = 1, message = "Minimo 1 credito")
        @Max(value = 10, message = "Maximo 10 creditos")
        Integer creditos,

        @NotNull(message = "El cupo maximo es obligatorio")
        @Min(value = 1, message = "El cupo debe ser mayor a 0")
        Integer cupoMaximo
) {
}
