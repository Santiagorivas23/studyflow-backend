package co.studyflow.infrastructure;

import co.studyflow.dto.GeminiResponseDTO;
import co.studyflow.model.Mazo;
import co.studyflow.dto.TarjetaSugeridaDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeminiHttpClientTest {

    @Test
    public void parsearRespuesta_extraeTarjetas() {
        GeminiHttpClient client = new GeminiHttpClient();

        String fake = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"[{\\\"frente\\\":\\\"Q1\\\",\\\"reverso\\\":\\\"A1\\\",\\\"confianza\\\":0.9}]\"}]}}]}";

        Mazo mazo = new Mazo();
        mazo.setId("m1");

        GeminiResponseDTO resp = client.parsearRespuesta(fake, mazo);

        assertNotNull(resp);
        List<TarjetaSugeridaDTO> tarjetas = resp.getTarjetasSugeridas();
        assertEquals(1, tarjetas.size());
        assertEquals("Q1", tarjetas.get(0).getFrente());
        assertEquals("A1", tarjetas.get(0).getReverso());
    }
}
