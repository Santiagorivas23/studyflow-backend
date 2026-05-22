package co.studyflow.repository;

import co.studyflow.model.RegistroRespuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroRespuestaRepository extends JpaRepository<RegistroRespuesta, String> {
    List<RegistroRespuesta> findBySesionId(String sesionId);
    
    List<RegistroRespuesta> findByTarjetaId(String tarjetaId);
    
    @Query("SELECT rr FROM RegistroRespuesta rr WHERE rr.sesion.id = :sesionId ORDER BY rr.timestamp ASC")
    List<RegistroRespuesta> findRegistrosPorSesion(@Param("sesionId") String sesionId);
    
    @Query("SELECT COUNT(rr) FROM RegistroRespuesta rr WHERE rr.sesion.usuario.id = :usuarioId AND rr.timestamp >= :desde")
    long countAciertosDelUsuario(@Param("usuarioId") String usuarioId, @Param("desde") LocalDateTime desde);
}
