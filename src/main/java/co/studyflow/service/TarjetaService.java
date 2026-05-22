package co.studyflow.service;

import co.studyflow.dto.TarjetaDTO;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.model.Mazo;
import co.studyflow.model.Tarjeta;
import co.studyflow.patterns.factory.TarjetaFactory;
import co.studyflow.repository.MazoRepository;
import co.studyflow.repository.TarjetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarjetaService {
    private static final Logger logger = LoggerFactory.getLogger(TarjetaService.class);
    
    @Autowired
    private TarjetaRepository tarjetaRepository;
    
    @Autowired
    private MazoRepository mazoRepository;
    
    public List<Tarjeta> obtenerPorMazo(String mazoId) {
        logger.debug("Obteniendo tarjetas del mazo: {}", mazoId);
        return tarjetaRepository.findByMazoId(mazoId);
    }
    
    public Tarjeta obtenerPorId(String tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
    }
    
    public Tarjeta crear(TarjetaDTO tarjetaDTO) {
        Mazo mazo = mazoRepository.findById(tarjetaDTO.getMazoId())
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
        
        // Usar TarjetaFactory
        Tarjeta tarjeta = TarjetaFactory.crearTarjeta(
                tarjetaDTO.getFrente(),
                tarjetaDTO.getReverso(),
                tarjetaDTO.getTipo(),
                tarjetaDTO.getEtiquetas() != null ? String.join(",", tarjetaDTO.getEtiquetas()) : null,
                tarjetaDTO.getConPista(),
                tarjetaDTO.getPista()
        );
        
        tarjeta.setMazo(mazo);
        
        logger.info("Creando tarjeta en mazo: {}", mazo.getId());
        Tarjeta saved = tarjetaRepository.save(tarjeta);
        
        // Actualizar total de tarjetas del mazo
        mazo.setTotalTarjetas((mazo.getTotalTarjetas() != null ? mazo.getTotalTarjetas() : 0) + 1);
        mazoRepository.save(mazo);
        
        return saved;
    }
    
    public List<Tarjeta> crearMultiples(String mazoId, List<TarjetaDTO> tarjetasDTOs) {
        Mazo mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
        
        List<Tarjeta> tarjetas = tarjetasDTOs.stream()
                .map(dto -> {
                    Tarjeta t = TarjetaFactory.crearTarjeta(
                            dto.getFrente(),
                            dto.getReverso(),
                            dto.getTipo(),
                            dto.getEtiquetas() != null ? String.join(",", dto.getEtiquetas()) : null,
                            dto.getConPista(),
                            dto.getPista()
                    );
                    t.setMazo(mazo);
                    return t;
                })
                .collect(Collectors.toList());
        
        logger.info("Creando {} tarjetas en mazo: {}", tarjetas.size(), mazoId);
        List<Tarjeta> saved = tarjetaRepository.saveAll(tarjetas);
        
        // Actualizar total
        mazo.setTotalTarjetas((mazo.getTotalTarjetas() != null ? mazo.getTotalTarjetas() : 0) + tarjetas.size());
        mazoRepository.save(mazo);
        
        return saved;
    }
    
    public Tarjeta actualizar(String tarjetaId, TarjetaDTO tarjetaDTO) {
        Tarjeta tarjeta = obtenerPorId(tarjetaId);
        
        if (tarjetaDTO.getFrente() != null) {
            tarjeta.setFrente(tarjetaDTO.getFrente());
        }
        if (tarjetaDTO.getReverso() != null) {
            tarjeta.setReverso(tarjetaDTO.getReverso());
        }
        if (tarjetaDTO.getEtiquetas() != null) {
            tarjeta.setEtiquetas(String.join(",", tarjetaDTO.getEtiquetas()));
        }
        if (tarjetaDTO.getTipo() != null) {
            tarjeta.setTipo(tarjetaDTO.getTipo());
        }
        
        logger.info("Actualizando tarjeta: {}", tarjetaId);
        return tarjetaRepository.save(tarjeta);
    }
    
    public void eliminar(String tarjetaId) {
        Tarjeta tarjeta = obtenerPorId(tarjetaId);
        Mazo mazo = tarjeta.getMazo();
        
        logger.info("Eliminando tarjeta: {}", tarjetaId);
        tarjetaRepository.delete(tarjeta);
        
        // Actualizar total
        mazo.setTotalTarjetas((mazo.getTotalTarjetas() != null ? mazo.getTotalTarjetas() : 1) - 1);
        mazoRepository.save(mazo);
    }
    
    public List<Tarjeta> obtenerTarjetasPendientes(String mazoId) {
        LocalDateTime ahora = LocalDateTime.now();
        return tarjetaRepository.findTarjetasPendientes(mazoId, ahora);
    }
    
    public long obtenerTarjetasDominadas(String mazoId) {
        return tarjetaRepository.countTarjetasDominadas(mazoId);
    }
}
