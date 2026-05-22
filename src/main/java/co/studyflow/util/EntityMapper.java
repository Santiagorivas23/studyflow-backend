package co.studyflow.util;

import co.studyflow.dto.*;
import co.studyflow.model.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilidad para mapear entre entidades y DTOs
 */
public class EntityMapper {
    
    public static MazoDTO toMazoDTO(Mazo mazo) {
        MazoDTO dto = new MazoDTO();
        dto.setId(mazo.getId());
        dto.setNombre(mazo.getNombre());
        dto.setDescripcion(mazo.getDescripcion());
        dto.setEtiquetas(parseEtiquetas(mazo.getEtiquetas()));
        dto.setFechaCreacion(mazo.getFechaCreacion());
        dto.setUltimaModificacion(mazo.getUltimaModificacion());
        dto.setAlgoritmo(mazo.getAlgoritmo());
        dto.setUsuarioId(mazo.getUsuario() != null ? mazo.getUsuario().getId() : null);
        dto.setPublicado(mazo.getPublicado());
        dto.setTotalTarjetas(mazo.getTotalTarjetas());
        
        if (mazo.getTarjetas() != null) {
            dto.setTarjetas(mazo.getTarjetas().stream()
                    .map(EntityMapper::toTarjetaDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static TarjetaDTO toTarjetaDTO(Tarjeta tarjeta) {
        TarjetaDTO dto = new TarjetaDTO();
        dto.setId(tarjeta.getId());
        dto.setMazoId(tarjeta.getMazo() != null ? tarjeta.getMazo().getId() : null);
        dto.setFrente(tarjeta.getFrente());
        dto.setReverso(tarjeta.getReverso());
        dto.setEtiquetas(parseEtiquetas(tarjeta.getEtiquetas()));
        dto.setTipo(tarjeta.getTipo());
        dto.setFechaCreacion(tarjeta.getFechaCreacion());
        dto.setProximoRepaso(tarjeta.getProximoRepaso());
        dto.setNivelDificultad(tarjeta.getNivelDificultad());
        dto.setIntentosTotales(tarjeta.getIntentosTotales());
        dto.setAciertos(tarjeta.getAciertos());
        dto.setConPista(tarjeta.getConPista());
        dto.setPista(tarjeta.getPista());
        
        return dto;
    }
    
    public static SesionDTO toSesionDTO(Sesion sesion) {
        SesionDTO dto = new SesionDTO();
        dto.setId(sesion.getId());
        dto.setMazoId(sesion.getMazo() != null ? sesion.getMazo().getId() : null);
        dto.setUsuarioId(sesion.getUsuario() != null ? sesion.getUsuario().getId() : null);
        dto.setFechaInicio(sesion.getFechaInicio());
        dto.setFechaFin(sesion.getFechaFin());
        dto.setTarjetaActualIndex(sesion.getTarjetaActualIndex());
        dto.setCompletada(sesion.getCompletada());
        
        if (sesion.getMazo() != null && sesion.getMazo().getTarjetas() != null) {
            dto.setTarjetas(sesion.getMazo().getTarjetas().stream()
                    .map(EntityMapper::toTarjetaDTO)
                    .collect(Collectors.toList()));
        }
        
        if (sesion.getRegistros() != null) {
            dto.setRegistros(sesion.getRegistros().stream()
                    .map(EntityMapper::toRegistroRespuestaDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static RegistroRespuestaDTO toRegistroRespuestaDTO(RegistroRespuesta registro) {
        RegistroRespuestaDTO dto = new RegistroRespuestaDTO();
        dto.setId(registro.getId());
        dto.setTarjetaId(registro.getTarjeta() != null ? registro.getTarjeta().getId() : null);
        dto.setSesionId(registro.getSesion() != null ? registro.getSesion().getId() : null);
        dto.setCalificacion(registro.getCalificacion());
        dto.setTiempoRespuesta(registro.getTiempoRespuesta());
        dto.setTimestamp(registro.getTimestamp());
        
        return dto;
    }
    
    private static List<String> parseEtiquetas(String etiquetasStr) {
        if (etiquetasStr == null || etiquetasStr.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(etiquetasStr.split(","));
    }
}
