package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasSesionDTO {
    private Integer aciertos;
    private Integer total;
    private Double porcentajeAciertos;
}
