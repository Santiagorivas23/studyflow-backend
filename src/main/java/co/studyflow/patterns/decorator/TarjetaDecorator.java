package co.studyflow.patterns.decorator;

import co.studyflow.model.Tarjeta;

/**
 * Decorator base para enriquecer tarjetas
 */
public abstract class TarjetaDecorator extends Tarjeta {
    protected Tarjeta tarjetaDecorada;
    
    public TarjetaDecorator(Tarjeta tarjeta) {
        this.tarjetaDecorada = tarjeta;
        // Copiar propiedades de la tarjeta original
        this.setId(tarjeta.getId());
        this.setFrente(tarjeta.getFrente());
        this.setReverso(tarjeta.getReverso());
        this.setMazo(tarjeta.getMazo());
        this.setEtiquetas(tarjeta.getEtiquetas());
        this.setFechaCreacion(tarjeta.getFechaCreacion());
        this.setProximoRepaso(tarjeta.getProximoRepaso());
        this.setNivelDificultad(tarjeta.getNivelDificultad());
        this.setIntentosTotales(tarjeta.getIntentosTotales());
        this.setAciertos(tarjeta.getAciertos());
    }
    
    abstract public String obtenerFrenteEnriquecido();
    abstract public String obtenerReversoEnriquecido();
}
