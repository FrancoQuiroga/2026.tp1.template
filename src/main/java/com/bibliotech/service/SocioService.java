package main.java.com.bibliotech.service;

import main.java.com.bibliotech.exception.SocioException;
import main.java.com.bibliotech.model.Socio;

import java.util.List;

public interface SocioService {

    // Métodos de registro que advierten sobre posibles excepciones
    void registrarEstudiante(int dni, String nombre, String email) throws SocioException;
    void registrarDocente(int dni, String nombre, String email, String departamentoAsignado) throws SocioException;

    // Método de búsqueda seguro
    Socio buscarSocioPorDni(int dni) throws SocioException;

    // Listados
    List<Socio> obtenerTodosLosSocios();
    List<Socio> obtenerDocentes();
    List<Socio> obtenerEstudiantes();
}
