package com.pacientesimulado.application.repository;

import com.pacientesimulado.application.data.Disponibilidad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DisponibilidadRepository extends MongoRepository<Disponibilidad, String> {
    List<Disponibilidad> findByActorId(String actorId);
}
