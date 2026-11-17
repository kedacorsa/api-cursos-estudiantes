package com.uniajc.cursos.controller;

import com.uniajc.cursos.dto.EstudianteRequest;
import com.uniajc.cursos.dto.EstudianteResponse;
import com.uniajc.cursos.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "Lado N de la relacion: muchos estudiantes pertenecen a un curso")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    // ---------------- CRUD ----------------

    @Operation(summary = "Crear estudiante (el curso debe existir y tener cupo)")
    @PostMapping
    public ResponseEntity<EstudianteResponse> crear(@Valid @RequestBody EstudianteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.crear(request));
    }

    @Operation(summary = "Listar todos los estudiantes")
    @GetMapping
    public ResponseEntity<List<EstudianteResponse>> listar() {
        return ResponseEntity.ok(estudianteService.listar());
    }

    @Operation(summary = "Obtener estudiante por id")
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar estudiante (puede cambiarse de curso)")
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody EstudianteRequest request) {
        return ResponseEntity.ok(estudianteService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar estudiante")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estudianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Consultas personalizadas ----------------

    @Operation(summary = "Estudiantes de un curso (findByCurso_Codigo)")
    @GetMapping("/curso/{codigo}")
    public ResponseEntity<List<EstudianteResponse>> porCurso(@PathVariable String codigo) {
        return ResponseEntity.ok(estudianteService.listarPorCurso(codigo));
    }

    @Operation(summary = "Estudiantes de un curso desde cierto semestre (@Query JPQL)")
    @GetMapping("/curso/{codigo}/semestre-minimo/{semestre}")
    public ResponseEntity<List<EstudianteResponse>> porCursoYSemestre(@PathVariable String codigo,
                                                                      @PathVariable Integer semestre) {
        return ResponseEntity.ok(estudianteService.listarPorCursoYSemestreMinimo(codigo, semestre));
    }

    @Operation(summary = "Estudiantes en un rango de semestres")
    @GetMapping("/semestres")
    public ResponseEntity<List<EstudianteResponse>> porSemestres(@RequestParam Integer desde,
                                                                 @RequestParam Integer hasta) {
        return ResponseEntity.ok(estudianteService.listarPorRangoSemestre(desde, hasta));
    }

    @Operation(summary = "Buscar por nombres o apellidos (parcial)")
    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteResponse>> buscar(@RequestParam String texto) {
        return ResponseEntity.ok(estudianteService.buscarPorNombre(texto));
    }

    @Operation(summary = "Estudiantes inscritos en cursos con creditos >= N (JOIN)")
    @GetMapping("/creditos-minimos/{creditos}")
    public ResponseEntity<List<EstudianteResponse>> porCreditos(@PathVariable Integer creditos) {
        return ResponseEntity.ok(estudianteService.listarEnCursosConCreditosMinimos(creditos));
    }

    @Operation(summary = "Estudiantes por dominio de correo (consulta nativa SQL)")
    @GetMapping("/dominio")
    public ResponseEntity<List<EstudianteResponse>> porDominio(@RequestParam String dominio) {
        return ResponseEntity.ok(estudianteService.listarPorDominioEmail(dominio));
    }
}
