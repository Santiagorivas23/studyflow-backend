package co.studyflow.patterns.factory;

import co.studyflow.model.Tarjeta;
import co.studyflow.model.TipoTarjeta;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory Method para crear diferentes tipos de tarjetas
 */
public class TarjetaFactory {
    
    public static Tarjeta crearTarjeta(
            String frente, 
            String reverso, 
            TipoTarjeta tipo,
            String etiquetas,
            Boolean conPista,
            String pista
    ) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.randomUUID().toString());
        tarjeta.setFrente(frente);
        tarjeta.setReverso(reverso);
        tarjeta.setTipo(tipo != null ? tipo : TipoTarjeta.TEXTO);
        tarjeta.setEtiquetas(etiquetas);
        tarjeta.setFechaCreacion(LocalDateTime.now());
        tarjeta.setProximoRepaso(LocalDateTime.now());
        tarjeta.setNivelDificultad(0);
        tarjeta.setIntentosTotales(0);
        tarjeta.setAciertos(0);
        tarjeta.setConPista(conPista != null ? conPista : false);
        tarjeta.setPista(conPista ? pista : null);
        
        return tarjeta;
    }
    
    /**
     * Crea una tarjeta de tipo TEXTO
     */
    public static Tarjeta crearTarjetaTexto(String frente, String reverso, String etiquetas) {
        return crearTarjeta(frente, reverso, TipoTarjeta.TEXTO, etiquetas, false, null);
    }
    
    /**
     * Crea una tarjeta de tipo CÓDIGO
     */
    public static Tarjeta crearTarjetaCodigo(String frente, String reverso, String etiquetas) {
        return crearTarjeta(frente, reverso, TipoTarjeta.CODIGO, etiquetas, false, null);
    }
    
    /**
     * Crea una tarjeta con pista
     */
    public static Tarjeta crearTarjetaConPista(
            String frente, 
            String reverso, 
            String pista,
            TipoTarjeta tipo,
            String etiquetas
    ) {
        return crearTarjeta(frente, reverso, tipo, etiquetas, true, pista);
    }
}
