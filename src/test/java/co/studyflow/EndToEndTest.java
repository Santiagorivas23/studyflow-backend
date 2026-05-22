package co.studyflow;

import co.studyflow.model.Mazo;
import co.studyflow.model.Usuario;
import co.studyflow.repository.MazoRepository;
import co.studyflow.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@org.springframework.context.annotation.Import(co.studyflow.TestBeansConfig.class)
public class EndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MazoRepository mazoRepository;

    @Test
    public void flujoImportarYProgreso() {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID().toString());
        u.setNombre("E2EUser");
        u.setEmail("e2e@example.com");
        usuarioRepository.save(u);

        Mazo mazo = new Mazo();
        mazo.setId(UUID.randomUUID().toString());
        mazo.setNombre("E2EMazo");
        mazo.setUsuario(u);
        mazoRepository.save(mazo);

        String url = "http://localhost:" + port + "/api/mazos/importar";

        String contenido = "[{\"mazoId\":\"" + mazo.getId() + "\", \"frente\": \"Qe\", \"reverso\": \"Ae\"}]";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Usuario-Id", u.getId());

        org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(contenido.getBytes()) {
            @Override
            public String getFilename() {
                return "tarjetas.json";
            }
        };
        body.add("archivo", resource);

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
