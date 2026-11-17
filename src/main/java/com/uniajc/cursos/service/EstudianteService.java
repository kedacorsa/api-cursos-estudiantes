package com.uniajc.cursos.service;

import com.uniajc.cursos.dto.EstudianteRequest;
import com.uniajc.cursos.dto.EstudianteResponse;

import java.util.List;

public interface EstudianteService {
    EstudianteResponse crear(EstudianteRequest request);
    List<EstudianteResponse> listar();
    EstudianteResponse obtenerPorId(Long id);
    EstudianteResponse actualizar(Long id, EstudianteRequest request);
    void eliminar(Long id);

    // consultas personalizadas
    List<EstudianteResponse> listarPorCurso(String codigoCurso);
    List<EstudianteResponse> listarPorCursoYSemestreMinimo(String codigoCurso, Integer semestre);
    List<EstudianteResponse> listarPorRangoSemestre(Integer desde, Integer hasta);
    List<EstudianteResponse> buscarPorNombre(String texto);
    List<EstudianteResponse> listarEnCursosConCreditosMinimos(Integer creditos);
    List<EstudianteResponse> listarPorDominioEmail(String dominio);
}
