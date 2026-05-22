package co.studyflow.patterns.observer;

import co.studyflow.model.Sesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProgresoObserverManager {
    private static final Logger logger = LoggerFactory.getLogger(ProgresoObserverManager.class);
    private final List<ProgresoObserver> observers = new ArrayList<>();
    
    public void registrar(ProgresoObserver observer) {
        observers.add(observer);
        logger.debug("Observador registrado: {}", observer.getClass().getSimpleName());
    }
    
    public void desregistrar(ProgresoObserver observer) {
        observers.remove(observer);
    }
    
    public void notificarSesionCompletada(Sesion sesion) {
        for (ProgresoObserver observer : observers) {
            try {
                observer.onSesionCompletada(sesion);
            } catch (Exception e) {
                logger.error("Error notificando sesión completada: {}", e.getMessage());
            }
        }
    }
    
    public void notificarRachaActualizada(String usuarioId, int diasRacha) {
        for (ProgresoObserver observer : observers) {
            try {
                observer.onRachaActualizada(usuarioId, diasRacha);
            } catch (Exception e) {
                logger.error("Error notificando racha actualizada: {}", e.getMessage());
            }
        }
    }
    
    public void notificarTarjetasDominadasActualizado(String mazoId, int tarjetasDominadas) {
        for (ProgresoObserver observer : observers) {
            try {
                observer.onTarjetasDominadasActualizado(mazoId, tarjetasDominadas);
            } catch (Exception e) {
                logger.error("Error notificando tarjetas dominadas: {}", e.getMessage());
            }
        }
    }
}
