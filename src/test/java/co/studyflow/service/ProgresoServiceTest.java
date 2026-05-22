package co.studyflow.service;

import co.studyflow.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgresoServiceTest {

    @Mock
    private co.studyflow.repository.MazoRepository mazoRepository;

    @Mock
    private co.studyflow.repository.TarjetaRepository tarjetaRepository;

    @Mock
    private co.studyflow.repository.SesionRepository sesionRepository;

    @InjectMocks
    private ProgresoService progresoService;

    @Test
    public void obtenerEstadisticasGlobales_calculaCorrectamente() {
        String usuarioId = "user-1";

        Mazo mazo = new Mazo();
        mazo.setId("m1");
        mazo.setTotalTarjetas(5);

        when(mazoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(mazo));

        // Sesion de hoy con un registro
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(10);
        LocalDateTime fin = LocalDateTime.now();

        RegistroRespuesta reg = new RegistroRespuesta();
        reg.setId("r1");
        reg.setCalificacion(Calificacion.BIEN);

        Sesion sesion = new Sesion();
        sesion.setId("s1");
        sesion.setFechaInicio(inicio);
        sesion.setFechaFin(fin);
        sesion.setRegistros(List.of(reg));

        when(sesionRepository.findSesionesDelDia(usuarioId)).thenReturn(List.of(sesion));
        when(sesionRepository.countDiasConSesiones(eq(usuarioId), any(LocalDateTime.class))).thenReturn(7L);

        Map<String, Object> stats = progresoService.obtenerEstadisticasGlobales(usuarioId);

        assertEquals(1, stats.get("totalMazos"));
        assertEquals(5, stats.get("totalTarjetas"));
        assertEquals(7, stats.get("rachaActual"));
        assertEquals(1, stats.get("aciertosHoy"));
        assertEquals(1, stats.get("intentosHoy"));
        long esperado = Duration.between(inicio, fin).getSeconds();
        assertEquals(esperado, stats.get("tiempoEstudiadoHoy"));
    }
}
