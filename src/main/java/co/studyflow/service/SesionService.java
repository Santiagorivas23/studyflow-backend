package co.studyflow.service;

import co.studyflow.dto.SesionDTO;
import co.studyflow.exception.ResourceNotFoundException;
import co.studyflow.model.*;
import co.studyflow.patterns.observer.ProgresoObserverManager;
import co.studyflow.patterns.strategy.AlgoritmoRepeticion;
import co.studyflow.patterns.strategy.SM2Strategy;
import co.studyflow.patterns.strategy.LeitnerStrategy;
import co.studyflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SesionService {
    private static final Logger logger = LoggerFactory.getLogger(SesionService.class);

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private MazoRepository mazoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private RegistroRespuestaRepository registroRepository;

    @Autowired
    private ProgresoObserverManager observerManager;

    public Sesion iniciarSesion(String mazoId, String usuarioId) {
        Mazo mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Tarjeta> tarjetasPendientes = obtenerTarjetasPendientes(mazo);

        if (tarjetasPendientes.isEmpty()) {
            logger.warn("No hay tarjetas pendientes para el mazo: {}", mazoId);
            throw new IllegalArgumentException("No hay tarjetas pendientes para estudiar hoy");
        }

        Sesion sesion = new Sesion();
        sesion.setId(UUID.randomUUID().toString());
        sesion.setMazo(mazo);
        sesion.setUsuario(usuario);
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setTarjetaActualIndex(0);
        sesion.setCompletada(false);
        sesion.setRegistros(new ArrayList<>());

        logger.info("Iniciando sesión: {} con {} tarjetas pendientes", sesion.getId(), tarjetasPendientes.size());
        return sesionRepository.save(sesion);
    }

    @Transactional(readOnly = true)
    public Sesion obtenerSesion(String sesionId) {
        return sesionRepository.findById(sesionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));
    }

    public RegistroRespuesta registrarRespuesta(
            String sesionId,
            String tarjetaId,
            Calificacion calificacion,
            Integer tiempoRespuesta
    ) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));
        Tarjeta tarjeta = tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));

        RegistroRespuesta registro = new RegistroRespuesta();
        registro.setId(UUID.randomUUID().toString());
        registro.setTarjeta(tarjeta);
        registro.setSesion(sesion);
        registro.setCalificacion(calificacion);
        registro.setTiempoRespuesta(tiempoRespuesta != null ? tiempoRespuesta : 0);
        registro.setTimestamp(LocalDateTime.now());

        registro = registroRepository.save(registro);

        actualizarTarjeta(tarjeta, calificacion, sesion.getMazo().getAlgoritmo());

        // Advance session index and persist
        int nuevoIndice = (sesion.getTarjetaActualIndex() != null ? sesion.getTarjetaActualIndex() : 0) + 1;
        sesion.setTarjetaActualIndex(nuevoIndice);
        sesionRepository.save(sesion);

        logger.debug("Registro guardado para tarjeta: {}, calificación: {}, nuevo índice: {}",
                tarjetaId, calificacion, nuevoIndice);
        return registro;
    }

    public Sesion finalizarSesion(String sesionId) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));

        sesion.setFechaFin(LocalDateTime.now());
        sesion.setCompletada(true);

        logger.info("Sesión finalizada: {}", sesionId);
        sesion = sesionRepository.save(sesion);

        observerManager.notificarSesionCompletada(sesion);

        return sesion;
    }

    @Transactional(readOnly = true)
    public List<Tarjeta> obtenerTarjetasPendientesDeMazo(String mazoId) {
        LocalDateTime ahora = LocalDateTime.now();
        Mazo mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new ResourceNotFoundException("Mazo no encontrado"));
        return obtenerTarjetasPendientes(mazo);
    }

    @Transactional(readOnly = true)
    public List<Sesion> obtenerHistorial(String mazoId) {
        return sesionRepository.findByMazoIdAndCompletadaTrueOrderByFechaInicioDesc(mazoId);
    }

    private List<Tarjeta> obtenerTarjetasPendientes(Mazo mazo) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Tarjeta> tarjetas = mazo.getTarjetas();
        if (tarjetas == null || tarjetas.isEmpty()) {
            return new ArrayList<>();
        }
        return tarjetas.stream()
                .filter(t -> t.getProximoRepaso() == null || !t.getProximoRepaso().isAfter(ahora))
                .sorted(Comparator.comparing(
                        t -> t.getProximoRepaso() != null ? t.getProximoRepaso() : LocalDateTime.MIN))
                .collect(Collectors.toList());
    }

    private void actualizarTarjeta(Tarjeta tarjeta, Calificacion calificacion, TipoAlgoritmo algoritmo) {
        AlgoritmoRepeticion algo = (algoritmo == TipoAlgoritmo.LEITNER)
                ? new LeitnerStrategy()
                : new SM2Strategy();

        tarjeta.setIntentosTotales((tarjeta.getIntentosTotales() != null ? tarjeta.getIntentosTotales() : 0) + 1);

        if (calificacion == Calificacion.FACIL || calificacion == Calificacion.BIEN) {
            tarjeta.setAciertos((tarjeta.getAciertos() != null ? tarjeta.getAciertos() : 0) + 1);
        }

        algo.actualizarNivelDificultad(tarjeta, calificacion.getValor());
        LocalDateTime proximoRepaso = algo.calcularProximoRepaso(tarjeta, calificacion.getValor(), LocalDateTime.now());
        tarjeta.setProximoRepaso(proximoRepaso);

        tarjetaRepository.save(tarjeta);
    }
}
