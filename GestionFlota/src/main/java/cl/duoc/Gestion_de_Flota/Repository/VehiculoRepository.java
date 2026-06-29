package cl.duoc.Gestion_de_Flota.Repository;

import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {


     List<Vehiculo> findByActivoTrue();


    List<Vehiculo> findByActivoTrueAndDisponibleTrue();


    Optional<Vehiculo> findByIdAndActivoTrue(Long id);


    boolean existsByPatente(String patente);
}
