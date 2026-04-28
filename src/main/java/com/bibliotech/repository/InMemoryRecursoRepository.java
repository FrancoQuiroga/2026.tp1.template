package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Categoria;
import main.java.com.bibliotech.model.Recurso;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRecursoRepository implements RecursoRepository {

    // El Integer es el "ID de Inventario" único. El Recurso es el libro.
    private final Map<Integer, Recurso> baseDeDatos = new ConcurrentHashMap<>();

    // Esto es un generador de IDs auto-incremental (1, 2, 3...).
    // Es mucho mejor que usar un int normal porque es seguro si hay varios hilos.
    private final AtomicInteger generadorId = new AtomicInteger(1);

    // --- MÉTODOS DE LA INTERFAZ BASE (Repository) ---

    @Override
    public void guardar(Recurso recurso) {
        // Generamos un nuevo ID único para este ejemplar
        int idInventario = generadorId.getAndIncrement();

        // Guardamos el recurso en el mapa con su nuevo ID
        baseDeDatos.put(idInventario, recurso);
        System.out.println("Libro guardado en inventario con ID: " + idInventario);
    }

    @Override
    public Optional<Recurso> buscarPorId(Integer id) {
        // Busca directamente por el ID de inventario que generamos al guardar
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public List<Recurso> buscarTodos() {
        return List.copyOf(baseDeDatos.values());
    }

    // --- MÉTODOS ESPECÍFICOS DE LibrosRepository ---

    @Override
    public List<Recurso> findByTitulo(String titulo) {
        return baseDeDatos.values().stream()
                // Ignoramos mayúsculas y minúsculas para que sea más fácil buscar
                .filter(recurso -> recurso.titulo().equalsIgnoreCase(titulo))
                .toList(); // Característica moderna de Java para devolver la lista
    }

    @Override
    public List<Recurso> findByAutor(String autor) {
        return baseDeDatos.values().stream()
                .filter(recurso -> recurso.autor().equalsIgnoreCase(autor))
                .toList();
    }

    @Override
    public List<Recurso> findByCategoria(Categoria categoria) {
        return baseDeDatos.values().stream()
                .filter(recurso -> recurso.categoria() == categoria)
                .toList();
    }

    @Override
    public List<Recurso> findByIsbn(String isbn) {
        return baseDeDatos.values().stream()
                .filter(recurso -> recurso.isbn().equals(isbn))
                .toList();
    }

    @Override
    public List<Recurso> findByAnio(int anio) {
        return baseDeDatos.values().stream()
                .filter(recurso -> recurso.anio() == anio)
                .toList();
    }
}