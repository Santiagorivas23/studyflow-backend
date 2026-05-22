package co.studyflow.patterns.builder;

import co.studyflow.model.Mazo;
import co.studyflow.model.TipoAlgoritmo;
import co.studyflow.model.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builder Pattern para construir mazos de forma legible y flexible
 */
public class MazoBuilder {
    private String id;
    private String nombre;
    private String descripcion;
    private String etiquetas;
    private TipoAlgoritmo algoritmo;
    private Usuario usuario;
    private Boolean publicado;
    
    public MazoBuilder(String nombre, Usuario usuario) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.usuario = usuario;
        this.algoritmo = TipoAlgoritmo.SM2;
        this.publicado = false;
    }
    
    public MazoBuilder descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }
    
    public MazoBuilder etiquetas(String etiquetas) {
        this.etiquetas = etiquetas;
        return this;
    }
    
    public MazoBuilder algoritmo(TipoAlgoritmo algoritmo) {
        this.algoritmo = algoritmo;
        return this;
    }
    
    public MazoBuilder publicado(Boolean publicado) {
        this.publicado = publicado;
        return this;
    }
    
    public Mazo build() {
        Mazo mazo = new Mazo();
        mazo.setId(this.id);
        mazo.setNombre(this.nombre);
        mazo.setDescripcion(this.descripcion);
        mazo.setEtiquetas(this.etiquetas);
        mazo.setAlgoritmo(this.algoritmo);
        mazo.setUsuario(this.usuario);
        mazo.setPublicado(this.publicado);
        mazo.setFechaCreacion(LocalDateTime.now());
        mazo.setUltimaModificacion(LocalDateTime.now());
        mazo.setTarjetas(new ArrayList<>());
        mazo.setSesiones(new ArrayList<>());
        mazo.setTotalTarjetas(0);
        
        return mazo;
    }
}
