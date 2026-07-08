package cl.duoc.Gestion_de_Flota.Service;

import cl.duoc.Gestion_de_Flota.Exception.FlotaException;
import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VehiculoService {

    private static final Pattern PATENTE_PATTERN = Pattern.compile("^[A-Z0-9]{6,8}$");

    private final VehiculoRepository repository;

    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Vehiculo crear(Vehiculo vehiculo) {
        validarFormatoPatente(vehiculo.getPatente());

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

    public List<Vehiculo> listarPorMarca(String marca) {
        return repository.findByMarcaIgnoreCaseAndActivoTrue(marca);
    }

    public long contarDisponibles() {
        return repository.countDisponibles();
    }

    public Optional<Vehiculo> buscarPorId(Long id) {
        return repository.findByIdAndActivoTrue(id);
    }

    @Transactional
    public Optional<Vehiculo> actualizar(Long id, Vehiculo nuevo) {
        Optional<Vehiculo> existente = repository.findByIdAndActivoTrue(id);
        if (existente.isEmpty()) return Optional.empty();

        Vehiculo vehiculo = existente.get();

        if (nuevo.getPatente() != null && !nuevo.getPatente().equals(vehiculo.getPatente())) {
            validarFormatoPatente(nuevo.getPatente());
            if (repository.existsByPatente(nuevo.getPatente())) {
                throw new FlotaException(
                        "Ya existe otro vehículo con patente: " + nuevo.getPatente());
            }
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

    private void validarFormatoPatente(String patente) {
        if (patente == null || !PATENTE_PATTERN.matcher(patente).matches()) {
            throw new FlotaException(
                    "Formato de patente inválido. Debe contener solo letras mayúsculas "
                            + "y números, con una longitud de 6 a 8 caracteres: " + patente);
        }
    }
}