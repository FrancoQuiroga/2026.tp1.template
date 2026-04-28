package main.java.com.bibliotech.service;

import main.java.com.bibliotech.exception.PrestamoException;
import main.java.com.bibliotech.exception.RecursoException;
import main.java.com.bibliotech.exception.SocioException;
import main.java.com.bibliotech.model.Prestamo;
import main.java.com.bibliotech.model.Recurso;
import main.java.com.bibliotech.model.Socio;
import main.java.com.bibliotech.repository.PrestamoRepository;
import main.java.com.bibliotech.repository.RecursoRepository;
import main.java.com.bibliotech.repository.SocioRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PrestamoServiceImpl implements PrestamoService {

    private final RecursoRepository recursoRepository;
    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;

    public PrestamoServiceImpl(RecursoRepository recursoRepository,
                               SocioRepository socioRepository,
                               PrestamoRepository prestamoRepository) {
        this.recursoRepository = recursoRepository;
        this.socioRepository = socioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    public void realizarPrestamo(String isbn, int dniPrestatario) throws SocioException, RecursoException, PrestamoException {

        // 1. Validar el prestatario
        Socio prestatario = socioRepository.buscarPorId(dniPrestatario)
                .orElseThrow(() -> new SocioException("El socio con DNI " + dniPrestatario + " no se encuentra registrado."));

        // 2. Validar el recurso
        List<Recurso> ejemplaresDisponibles = recursoRepository.findByIsbn(isbn);
        if (ejemplaresDisponibles.isEmpty()) {
            throw new RecursoException("No existen ejemplares en el inventario con el ISBN: " + isbn);
        }
        Recurso recursoAPrestar = ejemplaresDisponibles.getFirst();

        // 3. Validar límite de préstamos del prestatario
        List<Prestamo> prestamosActivos = prestamoRepository.findBySocio(prestatario).stream()
                .filter(p -> p.fechaDevolucion() == null)
                .toList();

        if (prestamosActivos.size() >= prestatario.maxLibros()) {
            throw new SocioException("El prestatario " + prestatario.nombre() + " ha alcanzado su límite máximo de " + prestatario.maxLibros() + " libros.");
        }

        // 4. Construir y guardar el préstamo
        LocalDate fechaEntrega = LocalDate.now();

        try {
            Prestamo nuevoPrestamo = new Prestamo(
                    null, // El ID se genera en el repositorio
                    recursoAPrestar,
                    fechaEntrega,
                    fechaEntrega.plusDays(14), // Ejemplo: Vence en 14 días
                    prestatario
            );
            prestamoRepository.guardar(nuevoPrestamo);

        } catch (IllegalArgumentException e) {
            throw new PrestamoException("Datos inválidos al generar el comprobante: " + e.getMessage());
        }
    }

    @Override
    public void registrarDevolucion(int idPrestamo) throws PrestamoException {
        // 1. Buscamos el préstamo original
        Prestamo prestamoActual = prestamoRepository.buscarPorId(idPrestamo)
                .orElseThrow(() -> new PrestamoException("El préstamo con ID " + idPrestamo + " no existe."));

        // 2. Validamos que no se haya devuelto ya
        if (prestamoActual.fechaDevolucion() == null) {
            throw new PrestamoException("Este préstamo ya fue registrado como devuelto anteriormente.");
        }

        LocalDate hoy = LocalDate.now();

        // 3. Cálculo automático de días de retraso
        long diasRetraso = ChronoUnit.DAYS.between(prestamoActual.fechaDevolucion(), hoy);
        if (diasRetraso > 0) {
            // Aquí puedes lanzar una alerta o aplicar la lógica del "Bonus" de sanciones
            System.out.println("⚠️ AVISO: El recurso se devolvió con " + diasRetraso + " días de retraso.");
        }

        try {
            // 4. Creamos una copia inmutable actualizada (Usamos record)
            Prestamo prestamoActualizado = new Prestamo(
                    prestamoActual.idPrestamo(),
                    prestamoActual.recurso(),
                    prestamoActual.fechaEntrega(),
                    hoy,
                    prestamoActual.prestatario() // ¡Actualizamos la fecha de devolución!
            );

            // 5. Lo guardamos (Al tener el mismo ID, el repositorio en memoria lo sobrescribirá)
            prestamoRepository.guardar(prestamoActualizado);

        } catch (IllegalArgumentException e) {
            throw new PrestamoException("Error al generar el comprobante de devolución: " + e.getMessage());
        }
    }

    @Override
    public List<Prestamo> obtenerPrestamosVencidos() {
        LocalDate hoy = LocalDate.now();

        return prestamoRepository.buscarTodos().stream()
                // Primero, nos aseguramos de que el libro NO haya sido devuelto
                .filter(prestamo -> prestamo.fechaDevolucion() != null)
                // Luego, verificamos si la fecha de vencimiento ya pasó (es anterior a hoy)
                .filter(prestamo -> prestamo.fechaDevolucion().isBefore(hoy))
                .toList();
    }
}