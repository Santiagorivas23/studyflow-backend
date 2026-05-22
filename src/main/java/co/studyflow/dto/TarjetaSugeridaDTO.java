package co.studyflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaSugeridaDTO {
    private String frente;
    private String reverso;
    private Double confianza;
}
