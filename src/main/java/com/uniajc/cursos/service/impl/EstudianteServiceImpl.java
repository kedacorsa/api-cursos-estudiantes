package com.uniajc.cursos.service.impl;

import com.uniajc.cursos.dto.EstudianteRequest;
import com.uniajc.cursos.dto.EstudianteResponse;
import com.uniajc.cursos.entity.Curso;
import com.uniajc.cursos.entity.Estudiante;
import com.uniajc.cursos.exception.RecursoNoEncontradoException;
import com.uniajc.cursos.exception.ReglaNegocioException;
import com.uniajc.cursos.repository.CursoRepository;
import com.uniajc.cursos.repository.EstudianteRepository;
import com.uniajc.cursos.service.EstudianteService;
import com.uniajc.cursos.service.TextoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public EstudianteServiceImpl(EstudianteRepository estudianteRepository, CursoRepository cursoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public EstudianteResponse crear(EstudianteRequest request) {
        String documento = request.documento().trim();
        String email = normalizarEmail(request.email());

        if (estudianteRepository.existsByDocumento(documento)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el documento " + documento);
        }
        if (estudianteRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("Ya existe un estudiante con el email " + email);
        }

        // El curso DEBE existir (relacion N:1) y tener cupo
        Curso curso = buscarCurso(request.codigoCurso());
        validarCupo(curso);

        Estudiante estudiante = new Estudiante();
        llenarDatos(estudiante, request, documento, email, curso);
        return toResponse(estudianteRepository.save(estudiante));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listar() {
        return estudianteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = buscar(id);
        String documento = request.documento().trim();
        String email = normalizarEmail(request.email());

        if (estudianteRepository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante con el documento " + documento);
        }
        if (estudianteRepository.existsByEmailAndIdNot(email, id)) {
            throw new ReglaNegocioException("Ya existe otro estudiante con el email " + email);
        }

        Curso nuevoCurso = buscarCurso(request.codigoCurso());
        // Solo se valida cupo si lo estan cambiando de curso
        if (!Objects.equals(nuevoCurso.getId(), estudiante.getCurso().getId())) {
            validarCupo(nuevoCurso);
        }

        llenarDatos(estudiante, request, documento, email, nuevoCurso);
        return toResponse(estudianteRepository.save(estudiante));
    }

    @Override
    public void eliminar(Long id) {
        // Estudiante es el lado dependiente, se puede borrar sin problema
        estudianteRepository.delete(buscar(id));
    }

    // ---------- consultas personalizadas ----------

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarPorCurso(String codigoCurso) {
        Curso curso = buscarCurso(codigoCurso);
        return estudianteRepository.findByCurso_CodigoOrderByApellidosAsc(curso.getCodigo())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarPorCursoYSemestreMinimo(String codigoCurso, Integer semestre) {
        Curso curso = buscarCurso(codigoCurso);
        return estudianteRepository.buscarPorCursoYSemestreMinimo(curso.getCodigo(), semestre)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarPorRangoSemestre(Integer desde, Integer hasta) {
        if (desde > hasta) throw new ReglaNegocioException("'desde' no puede ser mayor que 'hasta'");
        return estudianteRepository.findBySemestreBetweenOrderBySemestreAsc(desde, hasta)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> buscarPorNombre(String texto) {
        String t = TextoUtil.limpiarEspacios(texto);
        return estudianteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(t, t)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarEnCursosConCreditosMinimos(Integer creditos) {
        return estudianteRepository.buscarEnCursosConCreditosMinimos(creditos)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponse> listarPorDominioEmail(String dominio) {
        String d = dominio.trim().toLowerCase(Locale.ROOT).replaceFirst("^@", "");
        return estudianteRepository.buscarPorDominioEmail(d).stream().map(this::toResponse).toList();
    }

    // ---------- metodos privados ----------

    private Estudiante buscar(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el estudiante con id " + id));
    }

    private Curso buscarCurso(String codigoCurso) {
        String codigo = TextoUtil.codigo(codigoCurso);
        return cursoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el curso con codigo " + codigo + ". Debe crearlo primero."));
    }

    private void validarCupo(Curso curso) {
        long inscritos = estudianteRepository.countByCurso_Id(curso.getId());
        if (inscritos >= curso.getCupoMaximo()) {
            throw new ReglaNegocioException("El curso " + curso.getCodigo() + " no tiene cupos disponibles ("
                    + inscritos + "/" + curso.getCupoMaximo() + ")");
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void llenarDatos(Estudiante e, EstudianteRequest r, String documento, String email, Curso curso) {
        e.setDocumento(documento);
        e.setNombres(TextoUtil.formatoTitulo(r.nombres()));     // "  jUAN  carlos" -> "Juan Carlos"
        e.setApellidos(TextoUtil.formatoTitulo(r.apellidos()));
        e.setEmail(email);
        e.setSemestre(r.semestre());
        e.setCurso(curso);
    }

    private EstudianteResponse toResponse(Estudiante e) {
        // Al leer e.getCurso().getCodigo() el LAZY va a la BD a traer el curso
        return new EstudianteResponse(e.getId(), e.getDocumento(), e.getNombres(), e.getApellidos(),
                e.getEmail(), e.getSemestre(), e.getCurso().getCodigo(), e.getCurso().getNombre());
    }
}
