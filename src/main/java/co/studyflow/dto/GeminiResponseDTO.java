package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiResponseDTO {
    private String mazoId;
    private String resumen;
    private List<TarjetaSugeridaDTO> tarjetasSugeridas;
    private List<String> temasDetectados;
    private List<String> temasNoDetectados;
}
