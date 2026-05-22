package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.studyflow.model.Calificacion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaRegistroDTO {
    private String sesionId;
    private String tarjetaId;
    private Calificacion calificacion;
    private TarjetaDTO siguienteTarjeta;
    private Boolean sesionCompletada;
    private EstadisticasSesionDTO estadisticas;
}
