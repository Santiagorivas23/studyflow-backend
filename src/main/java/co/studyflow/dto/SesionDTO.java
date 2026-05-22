package co.studyflow.dto;

import co.studyflow.model.Calificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionDTO {
    private String id;
    private String mazoId;
    private String usuarioId;
    private List<TarjetaDTO> tarjetas;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer tarjetaActualIndex;
    private Boolean completada;
    private List<RegistroRespuestaDTO> registros;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SesionDeCreacionDTO {
    private String mazoId;
}


