package co.studyflow.controller;

import co.studyflow.model.Mazo;
import co.studyflow.model.Usuario;
import co.studyflow.repository.MazoRepository;
import co.studyflow.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
    ,"spring.main.allow-bean-definition-overriding=true"
})
@AutoConfigureMockMvc
@org.springframework.context.annotation.Import(co.studyflow.TestBeansConfig.class)
public class MazoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MazoRepository mazoRepository;

    @Test
    public void importar_creaTarjetas_enMazoExistente() throws Exception {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID().toString());
        u.setNombre("TestUser");
        u.setEmail("testuser@example.com");
        usuarioRepository.save(u);

        Mazo mazo = new Mazo();
        mazo.setId(UUID.randomUUID().toString());
        mazo.setNombre("MazoTest");
        mazo.setUsuario(u);
        mazoRepository.save(mazo);

        String contenido = "[{\"mazoId\":\"" + mazo.getId() + "\", \"frente\": \"Q\", \"reverso\": \"A\"}]";

        MockMultipartFile file = new MockMultipartFile("archivo", "tarjetas.json", MediaType.APPLICATION_JSON_VALUE, contenido.getBytes());

        mockMvc.perform(multipart("/mazos/importar").file(file).header("X-Usuario-Id", u.getId()))
                .andExpect(status().isCreated());
    }
}
