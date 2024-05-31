package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.repository.DisponibilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;

    @Autowired
    public DisponibilidadService(DisponibilidadRepository disponibilidadRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
    }

    public List<Disponibilidad> obtenerPorActorId(String actorId) {
        return disponibilidadRepository.findByActorId(actorId);
    }

    public Disponibilidad guardarDisponibilidad(Disponibilidad disponibilidad) {
        return disponibilidadRepository.save(disponibilidad);
    }

    public void eliminarDisponibilidad(String id) {
        disponibilidadRepository.deleteById(id);
    }

    public List< Disponibilidad> obtenerDisponibilidadesPorActorId ( String actorId ) {
        return disponibilidadRepository.findByActorId(actorId);
    }
}
