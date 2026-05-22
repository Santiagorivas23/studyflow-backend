package co.studyflow.model;

/**
 * Enumeración que define los tipos de algoritmos de repetición disponibles
 */
public enum TipoAlgoritmo {
    SM2("SM2 - Spaced Repetition Algorithm 2"),
    LEITNER("Leitner System"),
    ALEATORIO("Repaso Aleatorio");

    private final String descripcion;

    TipoAlgoritmo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
