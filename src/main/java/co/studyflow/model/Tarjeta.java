package co.studyflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una tarjeta de estudio
 */
@Entity
@Table(name = "tarjetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarjeta {
    @Id
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String frente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reverso;

    @Column
    private String etiquetas;  // JSON array as string

    @Column
    @Enumerated(EnumType.STRING)
    private TipoTarjeta tipo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "proximo_repaso", nullable = false)
    private LocalDateTime proximoRepaso;

    @Column(name = "nivel_dificultad")
    private Integer nivelDificultad;  // 0-5

    @Column(name = "intentos_totales")
    private Integer intentosTotales;

    @Column(name = "aciertos")
    private Integer aciertos;

    @Column(name = "con_pista")
    private Boolean conPista;

    @Column(columnDefinition = "TEXT")
    private String pista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mazo_id", nullable = false)
    private Mazo mazo;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.proximoRepaso == null) {
            this.proximoRepaso = LocalDateTime.now();
        }
        if (this.tipo == null) {
            this.tipo = TipoTarjeta.TEXTO;
        }
        if (this.nivelDificultad == null) {
            this.nivelDificultad = 0;
        }
        if (this.intentosTotales == null) {
            this.intentosTotales = 0;
        }
        if (this.aciertos == null) {
            this.aciertos = 0;
        }
        if (this.conPista == null) {
            this.conPista = false;
        }
    }
}
