package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.repository.DisponibilidadRepository;
import com.pacientesimulado.application.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisponibilidadService {
    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private ActorRepository actorRepository;

    public List<Disponibilidad> actualizarDisponibilidad(Long actorId, List<Disponibilidad> disponibilidades) {
        Actor actor = actorRepository.findById(actorId).orElseThrow(() -> new RuntimeException("Actor no encontrado"));
        disponibilidades.forEach(d -> d.setActor(actor));
        return disponibilidadRepository.saveAll(disponibilidades);
    }

    public List<Disponibilidad> obtenerDisponibilidades(Long actorId) {
        return disponibilidadRepository.findByActorId(actorId);
    }
}
