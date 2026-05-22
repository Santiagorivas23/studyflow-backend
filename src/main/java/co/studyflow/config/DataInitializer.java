package co.studyflow.config;

import co.studyflow.model.TipoAlgoritmo;
import co.studyflow.model.Usuario;
import co.studyflow.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Crea el usuario demo al arrancar si no existe
 */
@Component
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public static final String DEMO_USER_ID = "demo-user-001";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        if (!usuarioRepository.existsById(DEMO_USER_ID)) {
            Usuario demo = new Usuario();
            demo.setId(DEMO_USER_ID);
            demo.setNombre("Demo");
            demo.setApellido("User");
            demo.setEmail("demo@studyflow.co");
            demo.setFechaRegistro(LocalDateTime.now());
            demo.setAlgoritmoPorDefecto(TipoAlgoritmo.SM2);
            demo.setNotificacionesHabilitadas(true);
            demo.setTarjetasPorSesion(20);
            usuarioRepository.save(demo);
            logger.info("Usuario demo creado: {}", DEMO_USER_ID);
        }
    }
}
