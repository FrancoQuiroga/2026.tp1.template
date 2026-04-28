package main.java.com.bibliotech.repository;

import java.util.List;
import java.util.Optional;
/*Repositorio genérico destinado a ser usados por los servicios, inyectados en el constructor*/
public interface Repository<T, ID> {
    void guardar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();

}
