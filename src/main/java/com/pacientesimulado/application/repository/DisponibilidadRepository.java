package com.pacientesimulado.application.repository;

import com.pacientesimulado.application.data.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByActorId(Long actorId);
}
