package com.uniajc.cursos.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Lado "UNO" de la relacion: un Curso tiene MUCHOS estudiantes (1:N).
 */
@Entity
@Table(name = "kv_curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long id;

    /** Codigo unico del curso, siempre en MAYUSCULAS. Ej: PROG3 */
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    /** Nombre como se muestra. Ej: "Programación III" */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Nombre normalizado (minusculas, sin tildes, sin espacios dobles).
     * "Programación III", "programacion iii" y "PROGRAMACION  III" -> "programacion iii".
     * Es UNIQUE, asi la BD nunca acepta el mismo curso escrito distinto.
     */
    @Column(name = "nombre_clave", nullable = false, unique = true, length = 100)
    private String nombreClave;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "creditos", nullable = false)
    private Integer creditos;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    /** Relacion inversa (1:N). mappedBy apunta al atributo "curso" de Estudiante. */
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes = new ArrayList<>();

    public Curso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreClave() {
        return nombreClave;
    }

    public void setNombreClave(String nombreClave) {
        this.nombreClave = nombreClave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }
}
