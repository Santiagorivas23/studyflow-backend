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
 * Entidad que representa una sesión de estudio
 */
@Entity
@Table(name = "sesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sesion {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mazo_id", nullable = false)
    private Mazo mazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "tarjeta_actual_index")
    private Integer tarjetaActualIndex;

    @Column(name = "completada")
    private Boolean completada;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroRespuesta> registros;

    @PrePersist
    public void prePersist() {
        if (this.fechaInicio == null) {
            this.fechaInicio = LocalDateTime.now();
        }
        if (this.completada == null) {
            this.completada = false;
        }
        if (this.tarjetaActualIndex == null) {
            this.tarjetaActualIndex = 0;
        }
    }
}
