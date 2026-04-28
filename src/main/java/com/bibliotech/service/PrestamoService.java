package main.java.com.bibliotech.service;

import main.java.com.bibliotech.exception.PrestamoException;
import main.java.com.bibliotech.exception.RecursoException;
import main.java.com.bibliotech.exception.SocioException;
import main.java.com.bibliotech.model.Prestamo;

import java.util.List;

public interface PrestamoService {
    // Declaramos explícitamente las excepciones de negocio que pueden ocurrir
    void realizarPrestamo(String isbn, int dniPrestatario) throws SocioException, RecursoException, PrestamoException;

    void registrarDevolucion(int idPrestamo) throws PrestamoException;
    List<Prestamo> obtenerPrestamosVencidos() throws PrestamoException;
}