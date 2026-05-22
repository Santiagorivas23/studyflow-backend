package co.studyflow.patterns.decorator;

import co.studyflow.model.Tarjeta;

/**
 * Decorador que añade pista a una tarjeta
 */
public class ConPistaDecorator extends TarjetaDecorator {
    private String pista;
    
    public ConPistaDecorator(Tarjeta tarjeta, String pista) {
        super(tarjeta);
        this.pista = pista;
        this.setConPista(true);
        this.setPista(pista);
    }
    
    @Override
    public String obtenerFrenteEnriquecido() {
        return tarjetaDecorada.getFrente() + "\n💡 Pista: " + pista;
    }
    
    @Override
    public String obtenerReversoEnriquecido() {
        return tarjetaDecorada.getReverso();
    }
}
