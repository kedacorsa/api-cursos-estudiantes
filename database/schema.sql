-- =========================================================
--  API Cursos y Estudiantes  -  PostgreSQL
--  Relacion: CURSO (1) ----< (N) ESTUDIANTE
-- =========================================================

CREATE TABLE IF NOT EXISTS kv_curso (
    id_curso      BIGSERIAL     PRIMARY KEY,
    codigo        VARCHAR(20)   NOT NULL UNIQUE,
    nombre        VARCHAR(100)  NOT NULL,
    nombre_clave  VARCHAR(100)  NOT NULL UNIQUE,   -- nombre sin tildes/minusculas: evita "Programación" vs "programacion"
    descripcion   VARCHAR(255),
    creditos      INTEGER       NOT NULL CHECK (creditos BETWEEN 1 AND 10),
    cupo_maximo   INTEGER       NOT NULL CHECK (cupo_maximo > 0)
);

CREATE TABLE IF NOT EXISTS kv_estudiante (
    id_estudiante BIGSERIAL     PRIMARY KEY,
    documento     VARCHAR(20)   NOT NULL UNIQUE,
    nombres       VARCHAR(80)   NOT NULL,
    apellidos     VARCHAR(80)   NOT NULL,
    email         VARCHAR(120)  NOT NULL UNIQUE,
    semestre      INTEGER       NOT NULL CHECK (semestre BETWEEN 1 AND 10),
    id_curso      BIGINT        NOT NULL,
    CONSTRAINT fk_kv_estudiante_curso FOREIGN KEY (id_curso)
        REFERENCES kv_curso (id_curso) ON DELETE RESTRICT   -- no deja borrar un curso con estudiantes
);

CREATE INDEX IF NOT EXISTS idx_kv_estudiante_curso ON kv_estudiante (id_curso);
