package com.uniajc.cursos.exception;

/** Se lanza cuando se viola una regla (duplicados, sin cupo, borrar con dependencias) -> HTTP 409 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
