package main.java.com.bibliotech.model;

import java.time.LocalDate;
import java.util.Optional;

public record Prestamo(Optional<Integer> idPrestamo, Recurso recurso, LocalDate fechaEntrega, LocalDate fechaDevolucion,
                       Socio prestatario) {

}
