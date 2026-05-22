package co.studyflow.repository;

import co.studyflow.model.Mazo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MazoRepository extends JpaRepository<Mazo, String> {
    List<Mazo> findByUsuarioId(String usuarioId);
    
    Optional<Mazo> findByIdAndUsuarioId(String id, String usuarioId);
    
    @Query("SELECT m FROM Mazo m WHERE m.usuario.id = :usuarioId AND m.nombre = :nombre")
    Optional<Mazo> findByNombreAndUsuarioId(@Param("nombre") String nombre, @Param("usuarioId") String usuarioId);
    
    List<Mazo> findByPublicadoTrue();
}
