package co.studyflow.service;

import co.studyflow.dto.ProgresoDTO;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.model.Mazo;
import co.studyflow.model.Tarjeta;
import co.studyflow.repository.MazoRepository;
import co.studyflow.repository.SesionRepository;
import co.studyflow.repository.TarjetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProgresoService {
    private static final Logger logger = LoggerFactory.getLogger(ProgresoService.class);
    
    @Autowired
    private MazoRepository mazoRepository;
    
    @Autowired
    private TarjetaRepository tarjetaRepository;
    
    @Autowired
    private SesionRepository sesionRepository;
    
    public ProgresoDTO obtenerProgresoPorMazo(String mazoId) {
        Mazo mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
        
        LocalDateTime ahora = LocalDateTime.now();
        
        // Contar tarjetas por estado
        List<Tarjeta> tarjetas = mazo.getTarjetas() != null ? mazo.getTarjetas() : List.of();
        
        long tarjetasDominadas = tarjetas.stream()
                .filter(t -> t.getNivelDificultad() != null && t.getNivelDificultad() >= 4)
                .count();
        
        long tarjetasPendientes = tarjetas.stream()
                .filter(t -> t.getProximoRepaso() != null && t.getProximoRepaso().isBefore(ahora))
                .count();
        
        long tarjetasNuevas = tarjetas.stream()
                .filter(t -> t.getProximoRepaso() == null || t.getProximoRepaso().isAfter(ahora))
                .count();
        
        // Calcular porcentaje completado
        double porcentajeCompletado = tarjetas.isEmpty() ? 0 : (double) tarjetasDominadas / tarjetas.size() * 100;
        
        // Calcular racha
        int racha = calcularRacha(mazo.getUsuario().getId());
        
        // Obtener última sesión
        LocalDateTime ultimaSesion = obtenerUltimaSesion(mazoId);
        
        // Calcular próxima sesión (tarjeta más próxima a repasar)
        LocalDateTime proximaSesion = tarjetas.stream()
                .map(Tarjeta::getProximoRepaso)
                .filter(java.util.Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(ahora.plusDays(1));
        
        // Estadísticas de aciertos
        int aciertos = (int) tarjetas.stream()
                .mapToInt(t -> t.getAciertos() != null ? t.getAciertos() : 0)
                .sum();
        
        int intentos = (int) tarjetas.stream()
                .mapToInt(t -> t.getIntentosTotales() != null ? t.getIntentosTotales() : 0)
                .sum();
        
        double porcentajeAciertos = intentos == 0 ? 0 : (double) aciertos / intentos * 100;
        
        ProgresoDTO dto = new ProgresoDTO();
        dto.setMazoId(mazoId);
        dto.setUsuarioId(mazo.getUsuario().getId());
        dto.setTarjetasDominadas((int) tarjetasDominadas);
        dto.setTarjetasPendientes((int) tarjetasPendientes);
        dto.setTarjetasNuevas((int) tarjetasNuevas);
        dto.setPorcentajeCompletado(porcentajeCompletado);
        dto.setRacha(racha);
        dto.setUltimaSesion(ultimaSesion);
        dto.setProximaSesion(proximaSesion);
        dto.setAciertosTotal(aciertos);
        dto.setIntentosTotales(intentos);
        dto.setPorcentajeAciertos(porcentajeAciertos);
        
        return dto;
    }

        public java.util.Map<String, Object> obtenerEstadisticasGlobales(String usuarioId) {
                java.util.Map<String, Object> stats = new java.util.HashMap<>();

                // Mazos del usuario
                java.util.List<Mazo> mazos = mazoRepository.findByUsuarioId(usuarioId);
                int totalMazos = mazos.size();

                // Total tarjetas
                int totalTarjetas = mazos.stream()
                                .mapToInt(m -> m.getTotalTarjetas() != null ? m.getTotalTarjetas() : 0)
                                .sum();

                // Racha actual
                int rachaActual = calcularRacha(usuarioId);

                // Sesiones del dia
                java.util.List<co.studyflow.model.Sesion> sesionesHoy = sesionRepository.findSesionesDelDia(usuarioId);

                int aciertosHoy = 0;
                int intentosHoy = 0;
                long tiempoEstudiadoHoy = 0; // segundos

                for (co.studyflow.model.Sesion s : sesionesHoy) {
                        if (s.getRegistros() != null) {
                                for (co.studyflow.model.RegistroRespuesta r : s.getRegistros()) {
                                        intentosHoy++;
                                        if (r.getCalificacion() != null && r.getCalificacion().getValor() >= 3) {
                                                aciertosHoy++;
                                        }
                                }
                        }

                        if (s.getFechaFin() != null && s.getFechaInicio() != null) {
                                tiempoEstudiadoHoy += java.time.Duration.between(s.getFechaInicio(), s.getFechaFin()).getSeconds();
                        }
                }

                stats.put("totalMazos", totalMazos);
                stats.put("totalTarjetas", totalTarjetas);
                stats.put("rachaActual", rachaActual);
                stats.put("aciertosHoy", aciertosHoy);
                stats.put("intentosHoy", intentosHoy);
                stats.put("tiempoEstudiadoHoy", tiempoEstudiadoHoy);

                return stats;
        }
    
    private int calcularRacha(String usuarioId) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace30Dias = ahora.minusDays(30);
        
        long diasConSesiones = sesionRepository.countDiasConSesiones(usuarioId, hace30Dias);
        
        // Simplificación: retorna los días con sesiones
        return (int) diasConSesiones;
    }
    
    private LocalDateTime obtenerUltimaSesion(String mazoId) {
        List<co.studyflow.model.Sesion> sesiones = sesionRepository
                .findByMazoIdAndCompletadaTrueOrderByFechaInicioDesc(mazoId);
        
        return sesiones.isEmpty() ? null : sesiones.get(0).getFechaInicio();
    }
}
