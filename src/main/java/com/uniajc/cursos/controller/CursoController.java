package com.uniajc.cursos.controller;

import com.uniajc.cursos.dto.CursoRequest;
import com.uniajc.cursos.dto.CursoResponse;
import com.uniajc.cursos.dto.CursoResumen;
import com.uniajc.cursos.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Lado 1 de la relacion: un curso tiene muchos estudiantes")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // ---------------- CRUD ----------------

    @Operation(summary = "Crear curso (valida codigo y nombre duplicados)")
    @PostMapping
    public ResponseEntity<CursoResponse> crear(@Valid @RequestBody CursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(request));
    }

    @Operation(summary = "Listar todos los cursos")
    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar() {
        return ResponseEntity.ok(cursoService.listar());
    }

    @Operation(summary = "Obtener curso por id")
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar curso")
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
        return ResponseEntity.ok(cursoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar curso (solo si no tiene estudiantes)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Consultas personalizadas ----------------

    @Operation(summary = "Buscar curso por codigo (ej: prog3, PROG3)")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CursoResponse> porCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(cursoService.obtenerPorCodigo(codigo));
    }

    @Operation(summary = "Buscar cursos por nombre parcial, sin importar tildes ni mayusculas")
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponse>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(cursoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cursos en un rango de creditos")
    @GetMapping("/creditos")
    public ResponseEntity<List<CursoResponse>> porCreditos(@RequestParam Integer min, @RequestParam Integer max) {
        return ResponseEntity.ok(cursoService.buscarPorRangoCreditos(min, max));
    }

    @Operation(summary = "Cursos con cupos disponibles (@Query JPQL)")
    @GetMapping("/con-cupo")
    public ResponseEntity<List<CursoResponse>> conCupo() {
        return ResponseEntity.ok(cursoService.listarConCupo());
    }

    @Operation(summary = "Cursos con al menos N estudiantes (@Query con subconsulta)")
    @GetMapping("/minimo-estudiantes/{minimo}")
    public ResponseEntity<List<CursoResponse>> conMinimo(@PathVariable long minimo) {
        return ResponseEntity.ok(cursoService.listarConMinimoEstudiantes(minimo));
    }

    @Operation(summary = "Resumen: cantidad de estudiantes por curso (JOIN + GROUP BY)")
    @GetMapping("/resumen")
    public ResponseEntity<List<CursoResumen>> resumen() {
        return ResponseEntity.ok(cursoService.resumen());
    }
}
