package co.studyflow.patterns.strategy;

import co.studyflow.model.Tarjeta;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * Implementación del algoritmo Leitner
 */
public class LeitnerStrategy implements AlgoritmoRepeticion {
    
    private static final Random random = new Random();
    private static final int[] CAJA_INTERVALOS = {1, 3, 7, 14, 30};
    
    @Override
    public LocalDateTime calcularProximoRepaso(
            Tarjeta tarjeta,
            int calificacion,
            LocalDateTime ahora
    ) {
        int caja = Math.min(tarjeta.getNivelDificultad(), CAJA_INTERVALOS.length - 1);
        int dias = CAJA_INTERVALOS[caja];
        
        if (calificacion == 1) { // NO_LA_SUPE
            dias = 1;
        }
        
        return ahora.plus(dias, ChronoUnit.DAYS);
    }
    
    @Override
    public void actualizarNivelDificultad(Tarjeta tarjeta, int calificacion) {
        int cajaActual = tarjeta.getNivelDificultad();
        
        if (calificacion == 1) { // NO_LA_SUPE
            tarjeta.setNivelDificultad(0);
        } else if (calificacion >= 3) { // BIEN o FACIL
            tarjeta.setNivelDificultad(Math.min(4, cajaActual + 1));
        }
    }
    
    @Override
    public String getNombre() {
        return "Leitner System";
    }
}
