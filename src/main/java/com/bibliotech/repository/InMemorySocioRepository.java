package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Socio;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySocioRepository implements SocioRepository {

    // El DNI (Integer) es nuestra llave única
    private final Map<Integer, Socio> baseDeDatos = new ConcurrentHashMap<>();

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public void guardar(Socio socio) {
        // Usamos el DNI del socio directamente como llave
        baseDeDatos.put(socio.dni(), socio);
        System.out.println("Socio guardado con DNI: " + socio.dni());
    }

    @Override
    public Optional<Socio> buscarPorId(Integer dni) {
        return Optional.ofNullable(baseDeDatos.get(dni));
    }

    @Override
    public List<Socio> buscarTodos() {
        return List.copyOf(baseDeDatos.values());
    }

    // --- MÉTODOS ESPECÍFICOS DE SocioRepository ---

    @Override
    public List<Socio> findByEmail(String email) {
        return baseDeDatos.values().stream()
                .filter(socio -> socio.email().equalsIgnoreCase(email))
                .toList();
    }

    @Override
    public List<Socio> findByNombre(String nombre) {
        return baseDeDatos.values().stream()
                // Usamos contains() para buscar coincidencias parciales (ej: buscar "Fran" encuentra "Franco")
                .filter(socio -> socio.nombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public List<Socio> findByRol(Class<? extends Socio> tipoRol) {
        return baseDeDatos.values().stream()
                // ¡Aquí está la magia! isInstance() verifica si el objeto pertenece a la clase solicitada
                .filter(tipoRol::isInstance)
                .toList();
    }
}