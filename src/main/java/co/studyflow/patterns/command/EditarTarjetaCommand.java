package co.studyflow.patterns.command;

import co.studyflow.model.Tarjeta;
import co.studyflow.repository.TarjetaRepository;

/**
 * Comando para editar una tarjeta
 */
public class EditarTarjetaCommand implements Command {
    private final Tarjeta tarjetaNew;
    private final Tarjeta tarjetaOld;
    private final TarjetaRepository tarjetaRepository;
    
    public EditarTarjetaCommand(
            Tarjeta tarjetaNew,
            Tarjeta tarjetaOld,
            TarjetaRepository tarjetaRepository
    ) {
        this.tarjetaNew = tarjetaNew;
        this.tarjetaOld = tarjetaOld;
        this.tarjetaRepository = tarjetaRepository;
    }
    
    @Override
    public void execute() {
        tarjetaRepository.save(tarjetaNew);
    }
    
    @Override
    public void undo() {
        tarjetaRepository.save(tarjetaOld);
    }
    
    @Override
    public String getDescripcion() {
        return "Editar tarjeta: " + tarjetaNew.getFrente();
    }
}
