package com.pacientesimulado.application.security;

import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticatedUser {
      private final UsuarioRepository usuarioRepository;

        @Autowired
        public AuthenticatedUser(UsuarioRepository usuarioRepository) {
            this.usuarioRepository = usuarioRepository;
        }

        public Optional<Usuario> get() {
            // Aquí deberíamos obtener el usuario autenticado de la sesión
            // Esto es solo un ejemplo básico
            return Optional.empty();
        }

        public void logout() {
            // Implementar la lógica de cierre de sesión
        }
    }

}
