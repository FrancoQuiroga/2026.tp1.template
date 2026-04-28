package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Categoria;
import main.java.com.bibliotech.model.Recurso;

import java.util.List;

public interface RecursoRepository extends Repository<Recurso,Integer> {
    List<Recurso> findByTitulo(String titulo);
    List<Recurso> findByAutor(String autor);
    List<Recurso> findByCategoria(Categoria categoria);
    List<Recurso> findByIsbn(String isbn);
    List<Recurso> findByAnio(int anio);
}
