package main.java.com.bibliotech.service;


import main.java.com.bibliotech.exception.RecursoException;
import main.java.com.bibliotech.model.Categoria;
import main.java.com.bibliotech.model.Recurso;

import java.util.List;

public interface RecursoService {

    // Métodos para ingresar stock a la biblioteca
    void registrarLibroFisico(String isbn, String titulo, String autor, int anio, Categoria categoria, String ubicacionEstante) throws RecursoException;

    void registrarEbook(String isbn, String titulo, String autor, int anio, Categoria categoria, String formatoArchivo, double tamanoMb) throws RecursoException;

    // Búsquedas (El README pide búsqueda avanzada por título, autor o categoría)
    List<Recurso> buscarPorTitulo(String titulo);
    List<Recurso> buscarPorAutor(String autor);
    List<Recurso> buscarPorCategoria(Categoria categoria);

    // Búsquedas operativas
    List<Recurso> buscarPorIsbn(String isbn);
    Recurso buscarPorIdInventario(int id) throws RecursoException;
    List<Recurso> obtenerTodos();
}