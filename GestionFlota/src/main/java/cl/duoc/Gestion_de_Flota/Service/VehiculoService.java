package cl.duoc.Gestion_de_Flota.Service;

import cl.duoc.Gestion_de_Flota.Exception.FlotaException;
import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Capa de negocio del microservicio de Gestión de Flota.
 * Aplica soft delete: los vehículos eliminados quedan en BD con activo=false.
 * Valida unicidad de patente y disponibilidad antes de asignación.
 */
@Service
public class VehiculoService {

    private final VehiculoRepository repository;

    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public Vehiculo crear(Vehiculo vehiculo) {
        if (repository.existsByPatente(vehiculo.getPatente())) {
            throw new FlotaException(
                    "Ya existe un vehículo con patente: " + vehiculo.getPatente());
        }
        vehiculo.setDisponible(true);
        vehiculo.setActivo(true);
        return repository.save(vehiculo);
    }



    public List<Vehiculo> listar() {
        return repository.findByActivoTrue();
    }

    public List<Vehiculo> listarDisponibles() {
        return repository.findByActivoTrueAndDisponibleTrue();
    }



    public Optional<Vehiculo> buscarPorId(Long id) {
        return repository.findByIdAndActivoTrue(id);
    }



    @Transactional
    public Optional<Vehiculo> actualizar(Long id, Vehiculo nuevo) {
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isEmpty()) return Optional.empty();

        Vehiculo vehiculo = existente.get();


        if (nuevo.getPatente() != null
                && !nuevo.getPatente().equals(vehiculo.getPatente())
                && repository.existsByPatente(nuevo.getPatente())) {
            throw new FlotaException(
                    "Ya existe otro vehículo con patente: " + nuevo.getPatente());
        }

        if (nuevo.getPatente() != null)    vehiculo.setPatente(nuevo.getPatente());
        if (nuevo.getMarca() != null)      vehiculo.setMarca(nuevo.getMarca());
        if (nuevo.getModelo() != null)     vehiculo.setModelo(nuevo.getModelo());
        if (nuevo.getDisponible() != null) vehiculo.setDisponible(nuevo.getDisponible());

        return Optional.of(repository.save(vehiculo));
    }



    @Transactional
    public boolean eliminar(Long id) {
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isPresent()) {
            Vehiculo vehiculo = existente.get();
            vehiculo.setActivo(false);
            vehiculo.setDisponible(false);
            repository.save(vehiculo);
            return true;
        }
        return false;
    }



    @Transactional
    public Optional<Vehiculo> cambiarDisponibilidad(Long id, boolean disponible) {
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isEmpty()) return Optional.empty();

        Vehiculo vehiculo = existente.get();
        vehiculo.setDisponible(disponible);
        return Optional.of(repository.save(vehiculo));
    }
}
