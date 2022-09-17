package org.jplausi.springcloud.msvc.usuarios.repositories;

import java.util.Optional;

import org.jplausi.springcloud.msvc.usuarios.models.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface  UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
