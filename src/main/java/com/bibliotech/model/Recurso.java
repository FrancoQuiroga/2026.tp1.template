package main.java.com.bibliotech.model;

public interface Recurso {
    String isbn();
    String titulo();
    String autor();
    int anio();
    Categoria categoria();
}
