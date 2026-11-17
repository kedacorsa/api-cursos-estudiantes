# API REST — Cursos y Estudiantes

Parcial Programación III · Spring Boot 3 + JPA + PostgreSQL
Kevin Danilo Cordoba Salinas
Brandon Eduardo Muñoz
## Relación

```
CURSO (1) ───────< (N) ESTUDIANTE
```

- Un **curso** tiene muchos estudiantes → `@OneToMany(mappedBy = "curso")` en `Curso`
- Un **estudiante** pertenece a un solo curso → `@ManyToOne(fetch = LAZY)` + `@JoinColumn(name = "id_curso")` en `Estudiante`


## Lógica para evitar inconsistencias

| Regla | Cómo se hace |
|---|---|
| Código de curso único sin importar mayúsculas | Se guarda en MAYÚSCULAS (`prog3` → `PROG3`) + `UNIQUE` |
| Nombre de curso único aunque se escriba distinto | Columna `nombre_clave` sin tildes, en minúsculas y sin espacios dobles: `Programación III`, `PROGRAMACION  iii` → `programacion iii` + `UNIQUE` |
| Documento y email de estudiante únicos | `existsBy...` antes de guardar; el email se pasa a minúsculas |
| Nombres bien escritos | `"  jUAN  carlos"` → `"Juan Carlos"` |
| El curso debe existir | El estudiante se registra con `codigoCurso`; si no existe → 404 |
| Cupo del curso | No se inscribe si el curso está lleno; no se baja el cupo por debajo de los inscritos |
| Borrado seguro | No se borra un curso con estudiantes (409). Primero se borran o mueven los estudiantes |
| Datos válidos | Bean Validation (`@NotBlank`, `@Email`, `@Min`, `@Max`, `@Pattern`) → 400 con detalle por campo |

## Endpoints

### Cursos `/api/cursos`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/cursos` | Crear |
| GET | `/api/cursos` | Listar |
| GET | `/api/cursos/{id}` | Obtener |
| PUT | `/api/cursos/{id}` | Actualizar |
| DELETE | `/api/cursos/{id}` | Eliminar (solo sin estudiantes) |
| GET | `/api/cursos/codigo/{codigo}` | Por código |
| GET | `/api/cursos/buscar?nombre=` | Por nombre parcial (sin tildes) |
| GET | `/api/cursos/creditos?min=&max=` | Rango de créditos |
| GET | `/api/cursos/con-cupo` | Con cupos disponibles |
| GET | `/api/cursos/minimo-estudiantes/{n}` | Con al menos N estudiantes |
| GET | `/api/cursos/resumen` | Inscritos por curso |

### Estudiantes `/api/estudiantes`
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/estudiantes` | Crear |
| GET | `/api/estudiantes` | Listar |
| GET | `/api/estudiantes/{id}` | Obtener |
| PUT | `/api/estudiantes/{id}` | Actualizar / cambiar de curso |
| DELETE | `/api/estudiantes/{id}` | Eliminar |
| GET | `/api/estudiantes/curso/{codigo}` | De un curso |
| GET | `/api/estudiantes/curso/{codigo}/semestre-minimo/{s}` | De un curso desde semestre S |
| GET | `/api/estudiantes/semestres?desde=&hasta=` | Rango de semestres |
| GET | `/api/estudiantes/buscar?texto=` | Por nombre o apellido |
| GET | `/api/estudiantes/creditos-minimos/{n}` | En cursos con ≥ N créditos |
| GET | `/api/estudiantes/dominio?dominio=` | Por dominio de correo |
