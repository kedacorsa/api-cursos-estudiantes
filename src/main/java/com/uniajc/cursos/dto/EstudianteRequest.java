package com.uniajc.cursos.dto;

import jakarta.validation.constraints.*;

/**
 * Datos para crear / actualizar un estudiante.
 * El curso se envia por su CODIGO (ej: "PROG3") y no por ID,
 * porque el usuario no deberia saberse los ID de la BD.
 */
public record EstudianteRequest(
        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "\\d{6,15}", message = "El documento debe tener solo numeros (6 a 15 digitos)")
        String documento,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 80)
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80)
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        String email,

        @NotNull(message = "El semestre es obligatorio")
        @Min(value = 1, message = "Semestre minimo 1")
        @Max(value = 10, message = "Semestre maximo 10")
        Integer semestre,

        @NotBlank(message = "El codigo del curso es obligatorio")
        String codigoCurso
) {
}
