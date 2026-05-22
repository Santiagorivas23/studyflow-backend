package co.studyflow.service;

import co.studyflow.dto.MazoDTO;
import co.studyflow.exception.ConflictException;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.model.Mazo;
import co.studyflow.model.Usuario;
import co.studyflow.patterns.builder.MazoBuilder;
import co.studyflow.repository.MazoRepository;
import co.studyflow.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MazoService {
    private static final Logger logger = LoggerFactory.getLogger(MazoService.class);
    
    @Autowired
    private MazoRepository mazoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private co.studyflow.service.TarjetaService tarjetaService;
    
    public List<Mazo> obtenerMazosDelUsuario(String usuarioId) {
        logger.debug("Obteniendo mazos del usuario: {}", usuarioId);
        return mazoRepository.findByUsuarioId(usuarioId);
    }
    
    public Mazo obtenerPorId(String mazoId, String usuarioId) {
        logger.debug("Obteniendo mazo: {} del usuario: {}", mazoId, usuarioId);
        return mazoRepository.findByIdAndUsuarioId(mazoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
    }
    
    public Mazo crear(MazoDTO mazoDTO, String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Verificar si ya existe un mazo con este nombre
        mazoRepository.findByNombreAndUsuarioId(mazoDTO.getNombre(), usuarioId)
                .ifPresent(m -> {
                    throw new ConflictException("Ya existe un mazo con este nombre");
                });
        
        // Usar MazoBuilder
        Mazo mazo = new MazoBuilder(mazoDTO.getNombre(), usuario)
                .descripcion(mazoDTO.getDescripcion())
                .algoritmo(mazoDTO.getAlgoritmo())
                .build();
        
        logger.info("Creando nuevo mazo: {}", mazo.getNombre());
        return mazoRepository.save(mazo);
    }
    
    public Mazo actualizar(String mazoId, MazoDTO mazoDTO, String usuarioId) {
        Mazo mazo = obtenerPorId(mazoId, usuarioId);
        
        if (mazoDTO.getNombre() != null) {
            mazo.setNombre(mazoDTO.getNombre());
        }
        if (mazoDTO.getDescripcion() != null) {
            mazo.setDescripcion(mazoDTO.getDescripcion());
        }
        if (mazoDTO.getAlgoritmo() != null) {
            mazo.setAlgoritmo(mazoDTO.getAlgoritmo());
        }
        
        logger.info("Actualizando mazo: {}", mazoId);
        return mazoRepository.save(mazo);
    }
    
    public void eliminar(String mazoId, String usuarioId) {
        Mazo mazo = obtenerPorId(mazoId, usuarioId);
        logger.info("Eliminando mazo: {}", mazoId);
        mazoRepository.delete(mazo);
    }
    
    public Mazo duplicar(String mazoId, String usuarioId) {
        Mazo mazoOriginal = obtenerPorId(mazoId, usuarioId);
        Usuario usuario = mazoOriginal.getUsuario();
        
        // Crear nuevo mazo
        String nombreNuevo = mazoOriginal.getNombre() + " (Copia)";
        
        Mazo mazoDuplicado = new MazoBuilder(nombreNuevo, usuario)
                .descripcion(mazoOriginal.getDescripcion())
                .algoritmo(mazoOriginal.getAlgoritmo())
                .build();
        
        mazoDuplicado = mazoRepository.save(mazoDuplicado);
        
        // Duplicar tarjetas
        if (mazoOriginal.getTarjetas() != null && !mazoOriginal.getTarjetas().isEmpty()) {
            java.util.List<co.studyflow.dto.TarjetaDTO> dtos = new java.util.ArrayList<>();
            final String mazoDuplicadoId = mazoDuplicado.getId();
            mazoOriginal.getTarjetas().forEach(tarjetaOriginal -> {
                co.studyflow.dto.TarjetaDTO dto = new co.studyflow.dto.TarjetaDTO();
                dto.setMazoId(mazoDuplicadoId);
                dto.setFrente(tarjetaOriginal.getFrente());
                dto.setReverso(tarjetaOriginal.getReverso());
                if (tarjetaOriginal.getEtiquetas() != null) {
                    dto.setEtiquetas(java.util.Arrays.asList(tarjetaOriginal.getEtiquetas().split(",")));
                }
                dto.setTipo(tarjetaOriginal.getTipo());
                dto.setConPista(tarjetaOriginal.getConPista());
                dto.setPista(tarjetaOriginal.getPista());
                dtos.add(dto);
            });

            // Crear todas las tarjetas duplicadas usando TarjetaService
            tarjetaService.crearMultiples(mazoDuplicadoId, dtos);
            // Refrescar mazoDuplicado
            mazoDuplicado = mazoRepository.findById(mazoDuplicadoId).orElse(mazoDuplicado);
        }
        
        logger.info("Mazo duplicado: {} -> {}", mazoId, mazoDuplicado.getId());
        return mazoDuplicado;
    }
}
