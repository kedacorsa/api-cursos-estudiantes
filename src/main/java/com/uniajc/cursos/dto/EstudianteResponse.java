package com.uniajc.cursos.dto;

public record EstudianteResponse(
        Long id,
        String documento,
        String nombres,
        String apellidos,
        String email,
        Integer semestre,
        String codigoCurso,
        String nombreCurso
) {
}
