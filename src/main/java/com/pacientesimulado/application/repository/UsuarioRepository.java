package com.pacientesimulado.application.repository;

import com.pacientesimulado.application.data.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);
    List<Usuario> findByRol(String rol);

}
