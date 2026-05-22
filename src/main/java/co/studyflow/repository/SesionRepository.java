package co.studyflow.repository;

import co.studyflow.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, String> {
    List<Sesion> findByMazoId(String mazoId);
    
    List<Sesion> findByUsuarioId(String usuarioId);
    
    List<Sesion> findByMazoIdAndCompletadaTrueOrderByFechaInicioDesc(String mazoId);
    
    @Query("SELECT s FROM Sesion s WHERE s.usuario.id = :usuarioId AND s.completada = true AND DATE(s.fechaInicio) = CURRENT_DATE")
    List<Sesion> findSesionesDelDia(@Param("usuarioId") String usuarioId);
    
    @Query("SELECT COUNT(DISTINCT DATE(s.fechaInicio)) FROM Sesion s WHERE s.usuario.id = :usuarioId AND s.completada = true AND s.fechaInicio >= :desde")
    long countDiasConSesiones(@Param("usuarioId") String usuarioId, @Param("desde") LocalDateTime desde);
}
