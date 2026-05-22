package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRespuestaDTO {
    private String id;
    private String tarjetaId;
    private String sesionId;
    private co.studyflow.model.Calificacion calificacion;
    private Integer tiempoRespuesta;
    private java.time.LocalDateTime timestamp;
}
