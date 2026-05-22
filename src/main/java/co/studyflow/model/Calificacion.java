package co.studyflow.model;

/**
 * Enumeración que define las posibles calificaciones de una tarjeta
 */
public enum Calificacion {
    FACIL(4, 1.30, "Fácil - Domina bien el tema"),
    BIEN(3, 1.20, "Bien - Comprende el tema"),
    DIFICIL(2, 1.0, "Difícil - Necesita refuerzo"),
    NO_LA_SUPE(1, 2.5, "No la supo - Requiere atención inmediata");

    private final int valor;
    private final double factor;  // Factor para SM-2
    private final String descripcion;

    Calificacion(int valor, double factor, String descripcion) {
        this.valor = valor;
        this.factor = factor;
        this.descripcion = descripcion;
    }

    public int getValor() {
        return valor;
    }

    public double getFactor() {
        return factor;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
