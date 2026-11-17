package com.uniajc.cursos.dto;

public record CursoResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        Integer creditos,
        Integer cupoMaximo,
        long inscritos,
        long cuposDisponibles
) {
}
