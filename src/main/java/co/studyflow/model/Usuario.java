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
 * Entidad que representa un usuario del sistema
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String apellido;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "algoritmo_default")
    @Enumerated(EnumType.STRING)
    private TipoAlgoritmo algoritmoPorDefecto;

    @Column(name = "tarjetas_por_sesion")
    private Integer tarjetasPorSesion;

    @Column
    private String idioma;

    @Column
    private String tema;

    @Column(name = "notificaciones_habilitadas")
    private Boolean notificacionesHabilitadas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Mazo> mazos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Sesion> sesiones;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.algoritmoPorDefecto == null) {
            this.algoritmoPorDefecto = TipoAlgoritmo.SM2;
        }
        if (this.idioma == null) {
            this.idioma = "es";
        }
        if (this.tema == null) {
            this.tema = "light";
        }
        if (this.notificacionesHabilitadas == null) {
            this.notificacionesHabilitadas = true;
        }
    }
}
