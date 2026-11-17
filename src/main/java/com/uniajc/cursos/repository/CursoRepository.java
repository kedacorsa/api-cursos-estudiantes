package com.uniajc.cursos.repository;

import com.uniajc.cursos.dto.CursoResumen;
import com.uniajc.cursos.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // ---------- Consultas por NOMBRE DE METODO (query methods) ----------

    Optional<Curso> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreClave(String nombreClave);

    /** Para el UPDATE: ¿existe OTRO curso (id distinto) con ese codigo? */
    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByNombreClaveAndIdNot(String nombreClave, Long id);

    /** Busqueda parcial sin importar tildes ni mayusculas (se busca sobre la clave normalizada). */
    List<Curso> findByNombreClaveContainingOrderByNombreAsc(String texto);

    List<Curso> findByCreditosBetweenOrderByCreditosAsc(Integer min, Integer max);

    // ---------- Consultas con @Query (JPQL) ----------

    /** Cursos que todavia tienen cupo disponible. */
    @Query("SELECT c FROM Curso c WHERE SIZE(c.estudiantes) < c.cupoMaximo ORDER BY c.nombre")
    List<Curso> buscarConCupoDisponible();

    /** Resumen: cuantos estudiantes tiene cada curso (incluye cursos vacios gracias al LEFT JOIN). */
    @Query("""
            SELECT new com.uniajc.cursos.dto.CursoResumen(c.codigo, c.nombre, c.cupoMaximo, COUNT(e))
            FROM Curso c LEFT JOIN c.estudiantes e
            GROUP BY c.id, c.codigo, c.nombre, c.cupoMaximo
            ORDER BY COUNT(e) DESC, c.nombre ASC
            """)
    List<CursoResumen> resumenInscritos();

    /** Cursos que tienen al menos N estudiantes. */
    @Query("""
            SELECT c FROM Curso c
            WHERE (SELECT COUNT(e) FROM Estudiante e WHERE e.curso = c) >= :minimo
            ORDER BY c.nombre
            """)
    List<Curso> buscarConMinimoEstudiantes(@Param("minimo") long minimo);
}
