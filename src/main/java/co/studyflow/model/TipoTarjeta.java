package co.studyflow.model;

/**
 * Enumeración que define los tipos de tarjetas disponibles
 */
public enum TipoTarjeta {
    TEXTO("Tarjeta de Texto"),
    CODIGO("Tarjeta de Código");

    private final String descripcion;

    TipoTarjeta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
