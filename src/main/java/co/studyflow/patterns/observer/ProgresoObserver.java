package co.studyflow.patterns.observer;

import co.studyflow.model.Sesion;

/**
 * Interfaz Observer para patrones de observación
 */
public interface ProgresoObserver {
    void onSesionCompletada(Sesion sesion);
    void onRachaActualizada(String usuarioId, int diasRacha);
    void onTarjetasDominadasActualizado(String mazoId, int tarjetasDominadas);
}
