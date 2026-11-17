package com.uniajc.cursos.exception;

/** Se lanza cuando no existe el registro buscado -> HTTP 404 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
