package co.studyflow.service;

import co.studyflow.model.Mazo;
import co.studyflow.model.Tarjeta;
import co.studyflow.model.TipoTarjeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TarjetaServiceTest {

    @Mock
    private co.studyflow.repository.TarjetaRepository tarjetaRepository;

    @Mock
    private co.studyflow.repository.MazoRepository mazoRepository;

    @InjectMocks
    private TarjetaService tarjetaService;

    @Test
    public void crear_incrementaTotalTarjetas() {
        String mazoId = "m1";
        co.studyflow.dto.TarjetaDTO dto = new co.studyflow.dto.TarjetaDTO();
        dto.setMazoId(mazoId);
        dto.setFrente("F");
        dto.setReverso("R");
        dto.setConPista(false);
        dto.setTipo(TipoTarjeta.TEXTO);

        Mazo mazo = new Mazo();
        mazo.setId(mazoId);
        mazo.setTotalTarjetas(0);

        when(mazoRepository.findById(mazoId)).thenReturn(Optional.of(mazo));

        when(tarjetaRepository.save(any(Tarjeta.class))).thenAnswer(inv -> inv.getArgument(0));

        Tarjeta created = tarjetaService.crear(dto);

        assertNotNull(created);
        verify(mazoRepository, times(1)).save(mazo);
        assertEquals(1, mazo.getTotalTarjetas());
    }

    @Test
    public void crearMultiples_actualizaTotal() {
        String mazoId = "m2";
        co.studyflow.dto.TarjetaDTO dto1 = new co.studyflow.dto.TarjetaDTO();
        dto1.setMazoId(mazoId);
        dto1.setFrente("A");
        dto1.setReverso("B");
        dto1.setConPista(false);

        co.studyflow.dto.TarjetaDTO dto2 = new co.studyflow.dto.TarjetaDTO();
        dto2.setMazoId(mazoId);
        dto2.setFrente("C");
        dto2.setReverso("D");
        dto2.setConPista(false);

        Mazo mazo = new Mazo();
        mazo.setId(mazoId);
        mazo.setTotalTarjetas(2);

        when(mazoRepository.findById(mazoId)).thenReturn(Optional.of(mazo));
        when(tarjetaRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Tarjeta> saved = tarjetaService.crearMultiples(mazoId, List.of(dto1, dto2));

        assertEquals(2, saved.size());
        assertEquals(4, mazo.getTotalTarjetas());
        verify(mazoRepository, times(1)).save(mazo);
    }
}
