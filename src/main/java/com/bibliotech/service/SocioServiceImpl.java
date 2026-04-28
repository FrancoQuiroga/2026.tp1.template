package main.java.com.bibliotech.service;

import main.java.com.bibliotech.exception.SocioException;
import main.java.com.bibliotech.model.Docente;
import main.java.com.bibliotech.model.Estudiante;
import main.java.com.bibliotech.model.Socio;
import main.java.com.bibliotech.repository.SocioRepository;

import java.util.List;

public class SocioServiceImpl implements SocioService {

    // Dependencia hacia la interfaz del repositorio, no hacia la implementación
    private final SocioRepository socioRepository;

    public SocioServiceImpl(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @Override
    public void registrarEstudiante(int dni, String nombre, String email) throws SocioException {
        validarDniUnico(dni);

        try {
            // El record Estudiante validará internamente el formato del email y largo del DNI
            Estudiante nuevoEstudiante = new Estudiante(dni, nombre, email);
            socioRepository.guardar(nuevoEstudiante);

        } catch (IllegalArgumentException e) {
            // Atrapamos la excepción genérica de Java y lanzamos nuestra excepción de negocio
            throw new SocioException("Error al registrar estudiante: " + e.getMessage());
        }
    }

    @Override
    public void registrarDocente(int dni, String nombre, String email, String departamentoAsignado) throws SocioException {
        validarDniUnico(dni);

        try {
            Docente nuevoDocente = new Docente(dni, nombre, email);
            socioRepository.guardar(nuevoDocente);

        } catch (IllegalArgumentException e) {
            throw new SocioException("Error al registrar docente: " + e.getMessage());
        }
    }

    @Override
    public Socio buscarSocioPorDni(int dni) throws SocioException {
        // Aprovechamos el Optional del repositorio. Si está vacío, lanzamos la excepción.
        return socioRepository.buscarPorId(dni)
                .orElseThrow(() -> new SocioException("No se encontró ningún socio registrado con el DNI: " + dni));
    }

    @Override
    public List<Socio> obtenerTodosLosSocios() {
        return socioRepository.buscarTodos();
    }

    @Override
    public List<Socio> obtenerDocentes() {
        // Usamos el método polimórfico que creamos en el repositorio
        return socioRepository.findByRol(Docente.class);
    }

    @Override
    public List<Socio> obtenerEstudiantes() {
        return socioRepository.findByRol(Estudiante.class);
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private void validarDniUnico(int dni) throws SocioException {
        // Si el Optional tiene algún valor (isPresent), significa que el DNI ya existe
        if (socioRepository.buscarPorId(dni).isPresent()) {
            throw new SocioException("Ya existe un socio registrado en el sistema con el DNI: " + dni);
        }
    }
}