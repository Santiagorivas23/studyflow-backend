package co.studyflow.patterns.command;

import co.studyflow.model.Tarjeta;
import co.studyflow.repository.TarjetaRepository;

/**
 * Comando para eliminar una tarjeta
 */
public class EliminarTarjetaCommand implements Command {
    private final Tarjeta tarjeta;
    private final TarjetaRepository tarjetaRepository;
    
    public EliminarTarjetaCommand(Tarjeta tarjeta, TarjetaRepository tarjetaRepository) {
        this.tarjeta = tarjeta;
        this.tarjetaRepository = tarjetaRepository;
    }
    
    @Override
    public void execute() {
        tarjetaRepository.delete(tarjeta);
    }
    
    @Override
    public void undo() {
        tarjetaRepository.save(tarjeta);
    }
    
    @Override
    public String getDescripcion() {
        return "Eliminar tarjeta: " + tarjeta.getFrente();
    }
}
