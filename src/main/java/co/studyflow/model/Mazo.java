package co.studyflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un mazo de estudio
 */
@Entity
@Table(name = "mazos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mazo {
    @Id
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private String etiquetas;  // JSON array as string

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    @Column(name = "algoritmo")
    @Enumerated(EnumType.STRING)
    private TipoAlgoritmo algoritmo;

    @Column(name = "publicado")
    private Boolean publicado;

    @Column(name = "total_tarjetas")
    private Integer totalTarjetas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "mazo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarjeta> tarjetas;

    @OneToMany(mappedBy = "mazo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sesion> sesiones;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        this.ultimaModificacion = LocalDateTime.now();
        if (this.algoritmo == null) {
            this.algoritmo = TipoAlgoritmo.SM2;
        }
        if (this.publicado == null) {
            this.publicado = false;
        }
        if (this.totalTarjetas == null) {
            this.totalTarjetas = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.ultimaModificacion = LocalDateTime.now();
        // Only recount if collection is already loaded to avoid LazyInitializationException
        if (tarjetas != null && org.hibernate.Hibernate.isInitialized(tarjetas)) {
            this.totalTarjetas = tarjetas.size();
        }
    }
}
