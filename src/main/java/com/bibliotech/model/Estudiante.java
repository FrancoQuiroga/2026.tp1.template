package main.java.com.bibliotech.model;

public record Estudiante(int dni, String email) implements Socio{
    @Override
    public int maxLibros() {
        return 3;
    }
}
