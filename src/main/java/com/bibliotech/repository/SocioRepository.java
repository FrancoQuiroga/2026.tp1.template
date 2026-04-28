package main.java.com.bibliotech.repository;

import main.java.com.bibliotech.model.Socio;

import java.util.List;

public interface SocioRepository extends Repository<Socio,Integer> {
    List<Socio> findByEmail(String email);
    List<Socio> findByRol(Class<? extends Socio> tipoRol);
    List<Socio> findByNombre(String nombre);

}
