package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Prestamo;
import main.java.com.bibliotech.model.Socio;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoRepository extends Repository<Prestamo,Integer>{
    List<Prestamo> findByIdPrestamo(int idPrestamo);
    //Buscar los préstamos que ocurren en una fecha en particular
    List<Prestamo> findByFechaParticular(LocalDate fecha);
    List<Prestamo> findBySocio(Socio socio);
    //Este es para buscar entre un rango de fechas, o cuando el préstamo está vencido/activo
    List<Prestamo> findByRangoFechas(LocalDate fechaInicial,LocalDate fechaFinal);

}
