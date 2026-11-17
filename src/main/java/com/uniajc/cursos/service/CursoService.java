package com.uniajc.cursos.service;

import com.uniajc.cursos.dto.CursoRequest;
import com.uniajc.cursos.dto.CursoResponse;
import com.uniajc.cursos.dto.CursoResumen;

import java.util.List;

public interface CursoService {
    CursoResponse crear(CursoRequest request);
    List<CursoResponse> listar();
    CursoResponse obtenerPorId(Long id);
    CursoResponse obtenerPorCodigo(String codigo);
    CursoResponse actualizar(Long id, CursoRequest request);
    void eliminar(Long id);

    // consultas personalizadas
    List<CursoResponse> buscarPorNombre(String texto);
    List<CursoResponse> buscarPorRangoCreditos(Integer min, Integer max);
    List<CursoResponse> listarConCupo();
    List<CursoResponse> listarConMinimoEstudiantes(long minimo);
    List<CursoResumen> resumen();
}
