package co.studyflow.patterns.composite;

import co.studyflow.model.Mazo;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Composite Pattern para manejar colecciones de mazos
 */
public class ColeccionMazos {
    private List<Mazo> mazos;
    
    public ColeccionMazos() {
        this.mazos = new ArrayList<>();
    }
    
    public ColeccionMazos(List<Mazo> mazos) {
        this.mazos = mazos;
    }
    
    public void agregarMazo(Mazo mazo) {
        mazos.add(mazo);
    }
    
    public void removerMazo(Mazo mazo) {
        mazos.remove(mazo);
    }
    
    public Iterator<Mazo> iterator() {
        return mazos.iterator();
    }
    
    public int getTotalMazos() {
        return mazos.size();
    }
    
    public int getTotalTarjetas() {
        return mazos.stream()
                .mapToInt(m -> m.getTotalTarjetas() != null ? m.getTotalTarjetas() : 0)
                .sum();
    }
    
    public List<Mazo> getMazos() {
        return new ArrayList<>(mazos);
    }
}
