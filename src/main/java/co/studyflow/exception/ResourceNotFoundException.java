package co.studyflow.exception;

/**
 * Excepción personalizada para cuando no se encuentra un recurso
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
    
    public ResourceNotFoundException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
