// UsuarioService.java
package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(String id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setCorreo(usuarioActualizado.getCorreo());
            usuario.setContraseña(usuarioActualizado.getContraseña());
            usuario.setRol(usuarioActualizado.getRol());
            return usuarioRepository.save(usuario);
        }
        throw new RuntimeException("Usuario no encontrado");
    }

    public Usuario obtenerUsuario(String id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public void eliminarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).orElse(null);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorCorreoOptional(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}
