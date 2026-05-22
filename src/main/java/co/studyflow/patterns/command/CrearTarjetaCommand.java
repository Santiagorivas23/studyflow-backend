package co.studyflow.patterns.command;

import co.studyflow.model.Tarjeta;
import co.studyflow.repository.TarjetaRepository;

/**
 * Comando para crear una tarjeta
 */
public class CrearTarjetaCommand implements Command {
    private final Tarjeta tarjeta;
    private final TarjetaRepository tarjetaRepository;
    
    public CrearTarjetaCommand(Tarjeta tarjeta, TarjetaRepository tarjetaRepository) {
        this.tarjeta = tarjeta;
        this.tarjetaRepository = tarjetaRepository;
    }
    
    @Override
    public void execute() {
        tarjetaRepository.save(tarjeta);
    }
    
    @Override
    public void undo() {
        tarjetaRepository.delete(tarjeta);
    }
    
    @Override
    public String getDescripcion() {
        return "Crear tarjeta: " + tarjeta.getFrente();
    }
}
