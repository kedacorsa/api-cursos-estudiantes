package com.uniajc.cursos.service.impl;

import com.uniajc.cursos.dto.CursoRequest;
import com.uniajc.cursos.dto.CursoResponse;
import com.uniajc.cursos.dto.CursoResumen;
import com.uniajc.cursos.entity.Curso;
import com.uniajc.cursos.exception.RecursoNoEncontradoException;
import com.uniajc.cursos.exception.ReglaNegocioException;
import com.uniajc.cursos.repository.CursoRepository;
import com.uniajc.cursos.repository.EstudianteRepository;
import com.uniajc.cursos.service.CursoService;
import com.uniajc.cursos.service.TextoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;

    public CursoServiceImpl(CursoRepository cursoRepository, EstudianteRepository estudianteRepository) {
        this.cursoRepository = cursoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public CursoResponse crear(CursoRequest request) {
        String codigo = TextoUtil.codigo(request.codigo());
        String clave = TextoUtil.clave(request.nombre());

        // Evitar duplicados aunque esten escritos distinto (tildes, mayusculas, espacios)
        if (cursoRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un curso con el codigo " + codigo);
        }
        if (cursoRepository.existsByNombreClave(clave)) {
            throw new ReglaNegocioException("Ya existe un curso con el nombre '" + request.nombre().trim() + "'");
        }

        Curso curso = new Curso();
        llenarDatos(curso, request, codigo, clave);
        return toResponse(cursoRepository.save(curso));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        return cursoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorCodigo(String codigo) {
        String cod = TextoUtil.codigo(codigo);
        return cursoRepository.findByCodigo(cod)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el curso con codigo " + cod));
    }

    @Override
    public CursoResponse actualizar(Long id, CursoRequest request) {
        Curso curso = buscar(id);
        String codigo = TextoUtil.codigo(request.codigo());
        String clave = TextoUtil.clave(request.nombre());

        // Validar duplicados contra OTROS cursos (excluyendo el mismo id)
        if (cursoRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ReglaNegocioException("Ya existe otro curso con el codigo " + codigo);
        }
        if (cursoRepository.existsByNombreClaveAndIdNot(clave, id)) {
            throw new ReglaNegocioException("Ya existe otro curso con el nombre '" + request.nombre().trim() + "'");
        }
        // No permitir bajar el cupo por debajo de los ya inscritos
        long inscritos = estudianteRepository.countByCurso_Id(id);
        if (request.cupoMaximo() < inscritos) {
            throw new ReglaNegocioException("El cupo no puede ser menor a los " + inscritos + " estudiantes inscritos");
        }

        llenarDatos(curso, request, codigo, clave);
        return toResponse(cursoRepository.save(curso));
    }

    @Override
    public void eliminar(Long id) {
        Curso curso = buscar(id);
        long inscritos = estudianteRepository.countByCurso_Id(id);
        // Integridad referencial: no se borra un curso que tiene estudiantes
        if (inscritos > 0) {
            throw new ReglaNegocioException("No se puede eliminar el curso " + curso.getCodigo()
                    + " porque tiene " + inscritos + " estudiante(s). Eliminelos o cambielos de curso primero.");
        }
        cursoRepository.delete(curso);
    }

    // ---------- consultas personalizadas ----------

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> buscarPorNombre(String texto) {
        return cursoRepository.findByNombreClaveContainingOrderByNombreAsc(TextoUtil.clave(texto))
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> buscarPorRangoCreditos(Integer min, Integer max) {
        if (min > max) throw new ReglaNegocioException("El minimo de creditos no puede ser mayor al maximo");
        return cursoRepository.findByCreditosBetweenOrderByCreditosAsc(min, max)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listarConCupo() {
        return cursoRepository.buscarConCupoDisponible().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listarConMinimoEstudiantes(long minimo) {
        return cursoRepository.buscarConMinimoEstudiantes(minimo).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResumen> resumen() {
        return cursoRepository.resumenInscritos();
    }

    // ---------- metodos privados ----------

    private Curso buscar(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el curso con id " + id));
    }

    private void llenarDatos(Curso curso, CursoRequest request, String codigo, String clave) {
        curso.setCodigo(codigo);
        curso.setNombre(TextoUtil.limpiarEspacios(request.nombre()));
        curso.setNombreClave(clave);
        curso.setDescripcion(TextoUtil.limpiarEspacios(request.descripcion()));
        curso.setCreditos(request.creditos());
        curso.setCupoMaximo(request.cupoMaximo());
    }

    private CursoResponse toResponse(Curso c) {
        long inscritos = c.getId() == null ? 0 : estudianteRepository.countByCurso_Id(c.getId());
        return new CursoResponse(c.getId(), c.getCodigo(), c.getNombre(), c.getDescripcion(),
                c.getCreditos(), c.getCupoMaximo(), inscritos, Math.max(0, c.getCupoMaximo() - inscritos));
    }
}
