package co.studyflow.patterns.observer;

import co.studyflow.model.RegistroRespuesta;
import co.studyflow.model.Sesion;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Implementación concreta del Observer que registra estadísticas al completar sesiones.
 * Se registra automáticamente en el ProgresoObserverManager al arrancar.
 */
@Component
public class EstadisticasObserver implements ProgresoObserver {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticasObserver.class);

    @Autowired
    private ProgresoObserverManager observerManager;

    @PostConstruct
    public void registrarse() {
        observerManager.registrar(this);
        logger.info("EstadisticasObserver registrado");
    }

    @Override
    public void onSesionCompletada(Sesion sesion) {
        List<RegistroRespuesta> registros = sesion.getRegistros();
        int total = registros != null ? registros.size() : 0;
        long aciertos = registros != null
                ? registros.stream()
                        .filter(r -> r.getCalificacion() != null && r.getCalificacion().getValor() >= 3)
                        .count()
                : 0;
        long duracionSegundos = (sesion.getFechaFin() != null && sesion.getFechaInicio() != null)
                ? Duration.between(sesion.getFechaInicio(), sesion.getFechaFin()).getSeconds()
                : 0;

        logger.info("Sesión completada [{}] — mazo: {} | respuestas: {} | aciertos: {} ({}%) | duración: {}s",
                sesion.getId(),
                sesion.getMazo() != null ? sesion.getMazo().getNombre() : "?",
                total,
                aciertos,
                total > 0 ? aciertos * 100 / total : 0,
                duracionSegundos);
    }

    @Override
    public void onRachaActualizada(String usuarioId, int diasRacha) {
        logger.info("Racha actualizada — usuario: {} | {} días consecutivos", usuarioId, diasRacha);
    }

    @Override
    public void onTarjetasDominadasActualizado(String mazoId, int tarjetasDominadas) {
        logger.info("Tarjetas dominadas — mazo: {} | total: {}", mazoId, tarjetasDominadas);
    }
}
