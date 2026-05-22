package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgresoDTO {
    private String mazoId;
    private String usuarioId;
    private Integer tarjetasDominadas;
    private Integer tarjetasPendientes;
    private Integer tarjetasNuevas;
    private Double porcentajeCompletado;
    private Integer racha;
    private LocalDateTime ultimaSesion;
    private LocalDateTime proximaSesion;
    private Integer aciertosTotal;
    private Integer intentosTotales;
    private Double porcentajeAciertos;
}


