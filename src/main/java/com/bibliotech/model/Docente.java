package main.java.com.bibliotech.model;

public record Docente (int dni,String email,String nombre) implements Socio{
    @Override
    public int maxLibros() {
        return 5;
    }
}
