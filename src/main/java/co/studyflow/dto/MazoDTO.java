package co.studyflow.dto;

import co.studyflow.model.TipoAlgoritmo;
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
public class MazoDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private List<String> etiquetas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaModificacion;
    private TipoAlgoritmo algoritmo;
    private String usuarioId;
    private Boolean publicado;
    private Integer totalTarjetas;
    private List<TarjetaDTO> tarjetas;
}


