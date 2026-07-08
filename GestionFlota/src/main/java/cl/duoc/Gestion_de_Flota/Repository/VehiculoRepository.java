package cl.duoc.Gestion_de_Flota.Repository;

import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    
    List<Vehiculo> findByActivoTrue();

    List<Vehiculo> findByActivoTrueAndDisponibleTrue();

    Optional<Vehiculo> findByIdAndActivoTrue(Long id);

    boolean existsByPatente(String patente);

    @Query("SELECT COUNT(v) > 0 FROM Vehiculo v WHERE v.patente = :patente AND v.activo = true")
    boolean existsByPatenteAndActivoTrue(@Param("patente") String patente);

    @Query("SELECT COUNT(v) FROM Vehiculo v WHERE v.activo = true AND v.disponible = true")
    long countDisponibles();

    List<Vehiculo> findByMarcaIgnoreCaseAndActivoTrue(String marca);

    @Modifying
    @Transactional
    @Query("UPDATE Vehiculo v SET v.disponible = :disponible WHERE v.id = :id AND v.activo = true")
    int updateDisponibilidad(@Param("id") Long id, @Param("disponible") boolean disponible);
}
