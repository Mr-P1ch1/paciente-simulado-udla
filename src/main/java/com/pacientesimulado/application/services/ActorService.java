package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    public Actor registrarActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Actor actualizarActor(Long id, Actor actorActualizado) {
        Optional<Actor> actorOptional = actorRepository.findById(id);
        if (actorOptional.isPresent()) {
            Actor actor = actorOptional.get();
            actor.setSexo(actorActualizado.getSexo());
            actor.setEdad(actorActualizado.getEdad());
            actor.setTalla(actorActualizado.getTalla());
            actor.setAltura(actorActualizado.getAltura());
            return actorRepository.save(actor);
        }
        throw new RuntimeException("Actor no encontrado");
    }

    public Actor obtenerActor(Long id) {
        return actorRepository.findById(id).orElseThrow(() -> new RuntimeException("Actor no encontrado"));
    }
}

