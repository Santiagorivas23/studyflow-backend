package co.studyflow.dto;

import co.studyflow.model.TipoTarjeta;
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
public class TarjetaDTO {
    private String id;
    private String mazoId;
    private String frente;
    private String reverso;
    private List<String> etiquetas;
    private TipoTarjeta tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime proximoRepaso;
    private Integer nivelDificultad;
    private Integer intentosTotales;
    private Integer aciertos;
    private Boolean conPista;
    private String pista;
}


