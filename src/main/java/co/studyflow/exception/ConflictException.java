package co.studyflow.exception;

/**
 * Excepción personalizada para conflictos de datos
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
    
    public ConflictException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
