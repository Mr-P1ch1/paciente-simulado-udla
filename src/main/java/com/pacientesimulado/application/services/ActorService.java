package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public List<Actor> obtenerTodosLosActores() {
        return actorRepository.findAll();
    }

    public Optional<Actor> obtenerActorPorId(String id) {
        return actorRepository.findById(id);
    }

    public Optional<Actor> obtenerActorPorCorreo(String correo) {
        return actorRepository.findByCorreo(correo);
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
