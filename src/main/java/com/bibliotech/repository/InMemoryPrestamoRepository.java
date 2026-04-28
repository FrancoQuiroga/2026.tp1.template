package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Prestamo;
import main.java.com.bibliotech.model.Socio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPrestamoRepository implements PrestamoRepository {

    // Nuestro mapa de base de datos usando el ID del préstamo como llave
    private final Map<Integer, Prestamo> baseDeDatos = new ConcurrentHashMap<>();

    // Generador automático de números de préstamo (1, 2, 3...)
    private final AtomicInteger generadorId = new AtomicInteger(1);

    // --- MÉTODOS BASE (CRUD) ---

    @Override
    public void guardar(Prestamo prestamo) {
        // En una aplicación real, aquí podrías verificar si el préstamo ya tiene un ID
        // para saber si estás creando uno nuevo o actualizando una devolución.
        // Para simplificar, generaremos un ID nuevo simulando un "insert".
        int idPrestamo = generadorId.getAndIncrement();

        baseDeDatos.put(idPrestamo, prestamo);
        System.out.println("Préstamo registrado con comprobante #" + idPrestamo);
    }

    @Override
    public Optional<Prestamo> buscarPorId(Integer id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public List<Prestamo> buscarTodos() {
        return List.copyOf(baseDeDatos.values());
    }

    // --- MÉTODOS ESPECÍFICOS DE PrestamoRepository ---

    @Override
    public List<Prestamo> findByIdPrestamo(int idPrestamo) {
        // Respetando tu interfaz que devuelve una lista.
        // Usamos buscarPorId() y si está presente, lo metemos en una lista.
        return buscarPorId(idPrestamo)
                .map(List::of) // Si existe, crea una lista de 1 elemento
                .orElseGet(List::of); // Si no existe, devuelve lista vacía
    }

    @Override
    public List<Prestamo> findByFechaParticular(LocalDate fecha) {
        return baseDeDatos.values().stream()
                // Compara que la fecha de inicio sea exactamente igual a la buscada
                .filter(prestamo -> prestamo.fechaEntrega().isEqual(fecha))
                .toList();
    }

    @Override
    public List<Prestamo> findBySocio(Socio socio) {
        return baseDeDatos.values().stream()
                // Comparamos por el DNI del socio, que es su identificador único
                .filter(prestamo -> prestamo.prestatario().dni() == socio.dni())
                .toList();
    }

    @Override
    public List<Prestamo> findByRangoFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        return baseDeDatos.values().stream()
                // Filtramos que la fecha de inicio esté DENTRO del rango (inclusivo)
                .filter(prestamo -> {
                    LocalDate fechaPrestamo = prestamo.fechaEntrega();
                    // No es antes de la inicial Y no es después de la final
                    return !fechaPrestamo.isBefore(fechaInicial) && !fechaPrestamo.isAfter(fechaFinal);
                })
                .toList();
    }
}