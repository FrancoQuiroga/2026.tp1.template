package main.java.com.bibliotech.service;

import main.java.com.bibliotech.exception.RecursoException;
import main.java.com.bibliotech.model.Categoria;
import main.java.com.bibliotech.model.Ebook;
import main.java.com.bibliotech.model.LibroFisico;
import main.java.com.bibliotech.model.Recurso;
import main.java.com.bibliotech.repository.RecursoRepository;

import java.util.List;

public class RecursoServiceImpl implements RecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoServiceImpl(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Override
    public void registrarLibroFisico(String isbn, String titulo, String autor, int anio, Categoria categoria, String ubicacionEstante) throws RecursoException {
        try {
            // Nota: No validamos que el ISBN ya exista, porque la biblioteca puede
            // comprar múltiples ejemplares físicos del mismo libro. ¡Cada uno tendrá un ID único en el repo!
            boolean estaPrestado = false;
            LibroFisico nuevoLibro = new LibroFisico(isbn, titulo, autor, anio, categoria, ubicacionEstante, estaPrestado);
            recursoRepository.guardar(nuevoLibro);

        } catch (IllegalArgumentException e) {
            throw new RecursoException("Datos inválidos al registrar el libro físico: " + e.getMessage());
        }
    }

    @Override
    public void registrarEbook(String isbn, String titulo, String autor, int anio, Categoria categoria, String formatoArchivo, double tamanoMb) throws RecursoException {
        // Regla de Negocio: A diferencia de los libros físicos, no tiene sentido tener
        // múltiples copias exactas de un E-book en la base de datos.
        List<Recurso> existentes = recursoRepository.findByIsbn(isbn);
        boolean yaExisteEbook = existentes.stream().anyMatch(r -> r instanceof Ebook);

        if (yaExisteEbook) {
            throw new RecursoException("El E-book con ISBN " + isbn + " ya se encuentra en el catálogo digital.");
        }

        try {
            Ebook nuevoEbook = new Ebook(isbn, titulo, autor, anio, categoria, formatoArchivo, tamanoMb);
            recursoRepository.guardar(nuevoEbook);

        } catch (IllegalArgumentException e) {
            throw new RecursoException("Datos inválidos al registrar el E-book: " + e.getMessage());
        }
    }

    @Override
    public List<Recurso> buscarPorTitulo(String titulo) {
        return recursoRepository.findByTitulo(titulo);
    }

    @Override
    public List<Recurso> buscarPorAutor(String autor) {
        return recursoRepository.findByAutor(autor);
    }

    @Override
    public List<Recurso> buscarPorCategoria(Categoria categoria) {
        return recursoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Recurso> buscarPorIsbn(String isbn) {
        return recursoRepository.findByIsbn(isbn);
    }

    @Override
    public Recurso buscarPorIdInventario(int id) throws RecursoException {
        return recursoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoException("No se encontró ningún recurso físico/digital con el ID de inventario: " + id));
    }

    @Override
    public List<Recurso> obtenerTodos() {
        return recursoRepository.buscarTodos();
    }
}