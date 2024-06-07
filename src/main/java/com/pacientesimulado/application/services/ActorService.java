// ActorService.java
package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private UsuarioService usuarioService;

    public ActorService(ActorRepository actorRepository, UsuarioService usuarioService) {
        this.actorRepository = actorRepository;
        this.usuarioService = usuarioService;
    }

    public List<Actor> obtenerTodosLosActores() {
        List<Actor> actores = actorRepository.findAll();
        actores.forEach(actor -> {
            // Obtener el nombre y apellido del usuario correspondiente al actor
            usuarioService.obtenerUsuarioPorCorreoOptional(actor.getCorreo()).ifPresent(usuario -> {
                actor.setNombre(usuario.getNombre() + " " + usuario.getApellido());
            });
            System.out.println("Actor obtenido: " + actor.getNombre() + ", Correo: " + actor.getCorreo());
        });
        return actores;
    }

    public Optional<Actor> obtenerActorPorId(String id) {
        return actorRepository.findById(id);
    }

    public Optional<Actor> obtenerActorPorCorreo(String correo) {
        Optional<Actor> actorOptional = actorRepository.findByCorreo(correo);
        actorOptional.ifPresent(actor -> {
            // Obtener el nombre y apellido del usuario correspondiente al actor
            usuarioService.obtenerUsuarioPorCorreoOptional(correo).ifPresent(usuario -> {
                actor.setNombre(usuario.getNombre() + " " + usuario.getApellido());
            });
        });
        return actorOptional;
    }

    public void eliminarActor(String id) {
        actorRepository.deleteById(id);
    }

    public Actor guardarActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Actor actualizarActor(String id, Actor actorActualizado) {
        Optional<Actor> optionalActor = actorRepository.findById(id);
        if (optionalActor.isPresent()) {
            Actor actor = optionalActor.get();
            actor.setNombre(actorActualizado.getNombre());
            actor.setCorreo(actorActualizado.getCorreo());
            actor.setEdad(actorActualizado.getEdad());
            actor.setSexo(actorActualizado.getSexo());
            actor.setPeso(actorActualizado.getPeso());
            actor.setAltura(actorActualizado.getAltura());
            return actorRepository.save(actor);
        } else {
            return null;
        }
    }

    public Actor obtenerPorId(String id) {
        return actorRepository.findById(id).orElse(null);
    }
}
