package main.java.com.bibliotech.model;

import java.time.LocalDate;

public record Prestamo(int idPrestamo, LocalDate fechaEntrega, LocalDate fechaDevolucion,
                       Socio prestatario) {}
