package com.uniajc.cursos.repository;

import com.uniajc.cursos.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // ---------- Consultas por NOMBRE DE METODO ----------

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByDocumentoAndIdNot(String documento, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    /** El "_" navega a la propiedad de la otra entidad: curso.codigo */
    List<Estudiante> findByCurso_CodigoOrderByApellidosAsc(String codigoCurso);

    long countByCurso_Id(Long idCurso);

    List<Estudiante> findBySemestreBetweenOrderBySemestreAsc(Integer desde, Integer hasta);

    List<Estudiante> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);

    // ---------- Consultas con @Query (JPQL) ----------

    /** Estudiantes de un curso que esten en un semestre igual o superior al indicado. */
    @Query("""
            SELECT e FROM Estudiante e
            WHERE e.curso.codigo = :codigo AND e.semestre >= :semestre
            ORDER BY e.semestre DESC, e.apellidos ASC
            """)
    List<Estudiante> buscarPorCursoYSemestreMinimo(@Param("codigo") String codigo,
                                                   @Param("semestre") Integer semestre);

    /** Estudiantes inscritos en cursos con creditos >= N (filtro sobre la otra entidad). */
    @Query("SELECT e FROM Estudiante e JOIN e.curso c WHERE c.creditos >= :creditos ORDER BY c.nombre, e.apellidos")
    List<Estudiante> buscarEnCursosConCreditosMinimos(@Param("creditos") Integer creditos);

    // ---------- Consulta NATIVA (SQL puro de PostgreSQL) ----------

    /** Estudiantes cuyo correo termina en un dominio, ej: "uniajc.edu.co" */
    @Query(value = "SELECT * FROM kv_estudiante WHERE email LIKE CONCAT('%@', :dominio) ORDER BY apellidos",
            nativeQuery = true)
    List<Estudiante> buscarPorDominioEmail(@Param("dominio") String dominio);
}
