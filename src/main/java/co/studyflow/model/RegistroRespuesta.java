package co.studyflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que registra una respuesta a una tarjeta durante una sesión
 */
@Entity
@Table(name = "registros_respuesta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRespuesta {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id", nullable = false)
    private Tarjeta tarjeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;

    @Column
    @Enumerated(EnumType.STRING)
    private Calificacion calificacion;

    @Column(name = "tiempo_respuesta")
    private Integer tiempoRespuesta;  // en segundos

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
