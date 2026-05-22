package co.studyflow.controller;

import co.studyflow.dto.TarjetaDTO;
import co.studyflow.model.Tarjeta;
import co.studyflow.service.TarjetaService;
import co.studyflow.util.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de tarjetas
 */
@RestController
@RequestMapping({"/tarjetas", "/mazos/{mazoId}/tarjetas"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://*.vercel.app"})
public class TarjetaController {
    
    @Autowired
    private TarjetaService tarjetaService;
    
    /**
     * GET /api/mazos/{mazoId}/tarjetas - Obtener tarjetas de un mazo
     */
    @GetMapping
    public ResponseEntity<List<TarjetaDTO>> obtenerPorMazo(@PathVariable(required = false) String mazoId) {
        if (mazoId == null) {
            throw new IllegalArgumentException("mazoId es requerido");
        }
        
        List<Tarjeta> tarjetas = tarjetaService.obtenerPorMazo(mazoId);
        List<TarjetaDTO> dtos = tarjetas.stream()
                .map(EntityMapper::toTarjetaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * GET /api/tarjetas/{id} - Obtener tarjeta específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaDTO> obtenerPorId(@PathVariable String id) {
        Tarjeta tarjeta = tarjetaService.obtenerPorId(id);
        return ResponseEntity.ok(EntityMapper.toTarjetaDTO(tarjeta));
    }
    
    /**
     * POST /api/tarjetas - Crear nueva tarjeta
     */
    @PostMapping
    public ResponseEntity<TarjetaDTO> crear(@RequestBody TarjetaDTO tarjetaDTO) {
        Tarjeta tarjeta = tarjetaService.crear(tarjetaDTO);
        return ResponseEntity.status(201).body(EntityMapper.toTarjetaDTO(tarjeta));
    }
    
    /**
     * POST /api/mazos/{mazoId}/tarjetas/crear-multiples - Crear múltiples tarjetas
     */
    @PostMapping("/crear-multiples")
    public ResponseEntity<List<TarjetaDTO>> crearMultiples(
            @PathVariable String mazoId,
            @RequestBody java.util.Map<String, List<TarjetaDTO>> request
    ) {
        List<TarjetaDTO> tarjetasDTOs = request.get("tarjetas");
        List<Tarjeta> tarjetas = tarjetaService.crearMultiples(mazoId, tarjetasDTOs);
        
        List<TarjetaDTO> dtos = tarjetas.stream()
                .map(EntityMapper::toTarjetaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.status(201).body(dtos);
    }
    
    /**
     * PUT /api/tarjetas/{id} - Actualizar tarjeta
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarjetaDTO> actualizar(
            @PathVariable String id,
            @RequestBody TarjetaDTO tarjetaDTO
    ) {
        Tarjeta tarjeta = tarjetaService.actualizar(id, tarjetaDTO);
        return ResponseEntity.ok(EntityMapper.toTarjetaDTO(tarjeta));
    }
    
    /**
     * DELETE /api/tarjetas/{id} - Eliminar tarjeta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        tarjetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
