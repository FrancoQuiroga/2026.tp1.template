package main.java.com.bibliotech.model;

public record LibroFisico(String isbn,String titulo,String autor,int anio,
                          Categoria categoria, String ubicacionFisica) implements Recurso{}
