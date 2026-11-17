package com.uniajc.cursos.dto;

/** Proyeccion usada por una consulta JPQL con "select new". */
public record CursoResumen(String codigo, String nombre, Integer cupoMaximo, Long inscritos) {
}
