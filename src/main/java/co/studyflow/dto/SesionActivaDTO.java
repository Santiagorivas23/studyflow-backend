package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.studyflow.model.Calificacion;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionActivaDTO {
    private String sesionId;
    private TarjetaDTO tarjetaActual;
    private Integer totalTarjetas;
    private Integer tarjetasRestantes;
    private Integer indice;
}
