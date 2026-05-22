package co.studyflow.service;

import co.studyflow.model.Mazo;
import co.studyflow.model.Tarjeta;
import co.studyflow.model.TipoTarjeta;
import co.studyflow.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MazoServiceTest {

    @Mock
    private co.studyflow.repository.MazoRepository mazoRepository;

    @Mock
    private co.studyflow.repository.UsuarioRepository usuarioRepository;

    @Mock
    private co.studyflow.service.TarjetaService tarjetaService;

    @InjectMocks
    private MazoService mazoService;

    @Test
    public void duplicar_creaCopiaYDuplicaTarjetas() {
        String usuarioId = "user-1";
        String mazoId = "mazo-orig";

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Mazo original = new Mazo();
        original.setId(mazoId);
        original.setNombre("Original");
        original.setUsuario(usuario);

        Tarjeta t = new Tarjeta();
        t.setFrente("F");
        t.setReverso("R");
        t.setEtiquetas("a,b");
        t.setTipo(TipoTarjeta.TEXTO);

        original.setTarjetas(List.of(t));

        when(mazoRepository.findByIdAndUsuarioId(mazoId, usuarioId)).thenReturn(Optional.of(original));

        // Capture saved mazo
        AtomicReference<Mazo> savedRef = new AtomicReference<>();
        doAnswer(inv -> {
            Mazo arg = inv.getArgument(0);
            savedRef.set(arg);
            return arg;
        }).when(mazoRepository).save(any(Mazo.class));

        when(mazoRepository.findById(anyString())).thenAnswer(inv -> Optional.ofNullable(savedRef.get()));

        when(tarjetaService.crearMultiples(anyString(), anyList())).thenReturn(List.of());

        Mazo copia = mazoService.duplicar(mazoId, usuarioId);

        assertNotNull(copia);
        assertTrue(copia.getNombre().contains("Copia") || copia.getNombre().endsWith("(Copia)"));
        // Verify tarjetas duplicated
        assertNotNull(savedRef.get());
        verify(tarjetaService, times(1)).crearMultiples(eq(savedRef.get().getId()), anyList());
    }
}
