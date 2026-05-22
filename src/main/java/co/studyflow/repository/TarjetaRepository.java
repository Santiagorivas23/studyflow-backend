package co.studyflow.repository;

import co.studyflow.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, String> {
    List<Tarjeta> findByMazoId(String mazoId);
    
    @Query("SELECT t FROM Tarjeta t WHERE t.mazo.id = :mazoId AND t.proximoRepaso <= :ahora ORDER BY t.proximoRepaso ASC")
    List<Tarjeta> findTarjetasPendientes(@Param("mazoId") String mazoId, @Param("ahora") LocalDateTime ahora);
    
    @Query("SELECT COUNT(t) FROM Tarjeta t WHERE t.mazo.id = :mazoId AND t.nivelDificultad >= 4")
    long countTarjetasDominadas(@Param("mazoId") String mazoId);
    
    List<Tarjeta> findByMazoIdOrderByProximoRepasoAsc(String mazoId);
}
