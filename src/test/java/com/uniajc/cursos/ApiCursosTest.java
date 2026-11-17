package com.uniajc.cursos;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiCursosTest {

    @Autowired
    MockMvc mvc;

    private String curso(String codigo, String nombre, int creditos, int cupo) {
        return """
                {"codigo":"%s","nombre":"%s","descripcion":"desc","creditos":%d,"cupoMaximo":%d}
                """.formatted(codigo, nombre, creditos, cupo);
    }

    private String estudiante(String doc, String nombres, String email, int semestre, String curso) {
        return """
                {"documento":"%s","nombres":"%s","apellidos":"perez  gomez","email":"%s","semestre":%d,"codigoCurso":"%s"}
                """.formatted(doc, nombres, email, semestre, curso);
    }

    @Test
    @Order(1)
    void flujoCompleto() throws Exception {
        // Crear cursos
        mvc.perform(post("/api/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(curso(" prog3 ", "Programación  III", 4, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("PROG3"))
                .andExpect(jsonPath("$.nombre").value("Programación III"));
        mvc.perform(post("/api/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("BD1", "Bases de Datos", 3, 30)))
                .andExpect(status().isCreated());

        // Duplicado por nombre escrito distinto -> 409
        mvc.perform(post("/api/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("OTRO", "PROGRAMACION iii", 4, 10)))
                .andExpect(status().isConflict());
        // Duplicado por codigo -> 409
        mvc.perform(post("/api/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("Prog3", "Algo nuevo", 4, 10)))
                .andExpect(status().isConflict());
        // Validacion -> 400
        mvc.perform(post("/api/cursos").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("X", "", 0, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nombre").exists());

        // Estudiantes
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1001", "  jUAN  carlos", "Juan@UNIAJC.edu.co", 7, "prog3")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Perez Gomez"))
                .andExpect(jsonPath("$.email").value("juan@uniajc.edu.co"))
                .andExpect(jsonPath("$.codigoCurso").value("PROG3"));
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1002", "ana", "ana@gmail.com", 3, "PROG3")))
                .andExpect(status().isCreated());
        // Sin cupo (cupo 2) -> 409
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1003", "luis", "luis@gmail.com", 5, "PROG3")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("cupos")));
        // Curso inexistente -> 404
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1003", "luis", "luis@gmail.com", 5, "NOEXISTE")))
                .andExpect(status().isNotFound());
        // Email duplicado -> 409
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1004", "luis", "ANA@gmail.com", 5, "BD1")))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/estudiantes").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1005", "maria", "maria@uniajc.edu.co", 9, "BD1")))
                .andExpect(status().isCreated());

        // Consultas personalizadas
        mvc.perform(get("/api/cursos/buscar").param("nombre", "programacion"))
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/cursos/con-cupo")).andExpect(jsonPath("$[*].codigo", contains("BD1")));
        mvc.perform(get("/api/cursos/resumen")).andExpect(jsonPath("$[0].codigo").value("PROG3"))
                .andExpect(jsonPath("$[0].inscritos").value(2));
        mvc.perform(get("/api/cursos/minimo-estudiantes/2")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/cursos/creditos").param("min", "1").param("max", "3"))
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/cursos/codigo/prog3")).andExpect(jsonPath("$.cuposDisponibles").value(0));
        mvc.perform(get("/api/estudiantes/curso/prog3")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/estudiantes/curso/PROG3/semestre-minimo/5")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/estudiantes/semestres").param("desde", "1").param("hasta", "7"))
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/estudiantes/buscar").param("texto", "juan")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/estudiantes/creditos-minimos/4")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/estudiantes/dominio").param("dominio", "uniajc.edu.co"))
                .andExpect(jsonPath("$", hasSize(2)));

        // Update: bajar cupo por debajo de inscritos -> 409
        mvc.perform(put("/api/cursos/1").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("PROG3", "Programación III", 4, 1)))
                .andExpect(status().isConflict());
        // Update valido
        mvc.perform(put("/api/cursos/1").contentType(MediaType.APPLICATION_JSON)
                        .content(curso("PROG3", "Programación III", 4, 5)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cupoMaximo").value(5));
        // Mover estudiante de curso
        mvc.perform(put("/api/estudiantes/1").contentType(MediaType.APPLICATION_JSON)
                        .content(estudiante("1001", "juan carlos", "juan@uniajc.edu.co", 8, "BD1")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.codigoCurso").value("BD1"));

        // Borrar curso con estudiantes -> 409
        mvc.perform(delete("/api/cursos/2")).andExpect(status().isConflict());
        // Borrar estudiantes y luego el curso -> 204
        mvc.perform(delete("/api/estudiantes/1")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/estudiantes/3")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/cursos/2")).andExpect(status().isNoContent());
        mvc.perform(get("/api/cursos/2")).andExpect(status().isNotFound());
    }
}
