package cl.duoc.Gestion_de_Flota.Service;

import cl.duoc.Gestion_de_Flota.Exception.FlotaException;
import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoService.class);
    private final VehiculoRepository repository;

    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Vehiculo crear(Vehiculo vehiculo) {
        logger.info("Creando vehículo con patente: {}", vehiculo.getPatente());
        
        if (repository.existsByPatente(vehiculo.getPatente())) {
            logger.warn("Intento de duplicar patente: {}", vehiculo.getPatente());
            throw new FlotaException(
                    "Ya existe un vehículo con patente: " + vehiculo.getPatente());
        }
        
        if (!vehiculo.getPatente().matches("^[A-Z0-9]{6,8}$")) {
            throw new FlotaException(
                    "Formato de patente inválido. Debe tener 6-8 caracteres alfanuméricos mayúsculas.");
        }
        
        vehiculo.setDisponible(true);
        vehiculo.setActivo(true);
        Vehiculo guardado = repository.save(vehiculo);
        logger.info("Vehículo creado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }

    public List<Vehiculo> listar() {
        logger.debug("Listando todos los vehículos activos");
        return repository.findByActivoTrue();
    }

    public List<Vehiculo> listarDisponibles() {
        logger.debug("Listando vehículos disponibles");
        return repository.findByActivoTrueAndDisponibleTrue();
    }

    public Optional<Vehiculo> buscarPorId(Long id) {
        logger.debug("Buscando vehículo por ID: {}", id);
        Optional<Vehiculo> vehiculo = repository.findByIdAndActivoTrue(id);
        if (vehiculo.isPresent()) {
            logger.debug("Vehículo encontrado: {}", vehiculo.get().getPatente());
        } else {
            logger.debug("Vehículo no encontrado: {}", id);
        }
        return vehiculo;
    }

    @Transactional
    public Optional<Vehiculo> actualizar(Long id, Vehiculo nuevo) {
        logger.info("Actualizando vehículo ID: {}", id);
        
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isEmpty()) {
            logger.warn("Vehículo no encontrado para actualización: {}", id);
            return Optional.empty();
        }

        Vehiculo vehiculo = existente.get();
        logger.debug("Vehículo actual: {}", vehiculo.getPatente());

        
        if (nuevo.getPatente() != null
                && !nuevo.getPatente().equals(vehiculo.getPatente())
                && repository.existsByPatente(nuevo.getPatente())) {
            logger.warn("Intento de duplicar patente en actualización: {}", nuevo.getPatente());
            throw new FlotaException(
                    "Ya existe otro vehículo con patente: " + nuevo.getPatente());
        }

        
        if (nuevo.getPatente() != null) {
            
            if (!nuevo.getPatente().matches("^[A-Z0-9]{6,8}$")) {
                throw new FlotaException(
                        "Formato de patente inválido. Debe tener 6-8 caracteres alfanuméricos mayúsculas.");
            }
            vehiculo.setPatente(nuevo.getPatente());
        }
        if (nuevo.getMarca() != null) {
            vehiculo.setMarca(nuevo.getMarca());
        }
        if (nuevo.getModelo() != null) {
            vehiculo.setModelo(nuevo.getModelo());
        }
        if (nuevo.getDisponible() != null) {
            vehiculo.setDisponible(nuevo.getDisponible());
        }

        Vehiculo actualizado = repository.save(vehiculo);
        logger.info("Vehículo actualizado exitosamente: {}", actualizado.getPatente());
        return Optional.of(actualizado);
    }

    @Transactional
    public boolean eliminar(Long id) {
        logger.info("Eliminando (soft delete) vehículo ID: {}", id);
        
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isPresent()) {
            Vehiculo vehiculo = existente.get();
            vehiculo.setActivo(false);
            vehiculo.setDisponible(false);
            repository.save(vehiculo);
            logger.info("Vehículo marcado como inactivo: {}", id);
            return true;
        }
        
        logger.warn("Intento de eliminar vehículo inexistente: {}", id);
        return false;
    }

    @Transactional
    public Optional<Vehiculo> cambiarDisponibilidad(Long id, boolean disponible) {
        logger.info("Cambiando disponibilidad del vehículo ID {} a: {}", id, disponible);
        
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isEmpty()) {
            logger.warn("Vehículo no encontrado para cambiar disponibilidad: {}", id);
            return Optional.empty();
        }

        Vehiculo vehiculo = existente.get();
        vehiculo.setDisponible(disponible);
        Vehiculo actualizado = repository.save(vehiculo);
        logger.info("Disponibilidad actualizada para vehículo ID: {}", id);
        return Optional.of(actualizado);
    }

    public long contarDisponibles() {
        logger.debug("Contando vehículos disponibles");
        return repository.countDisponibles();
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        logger.debug("Listando vehículos de marca: {}", marca);
        return repository.findByMarcaIgnoreCaseAndActivoTrue(marca);
    }
}
