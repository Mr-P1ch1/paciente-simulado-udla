package com.pacientesimulado.application.repository;

import com.pacientesimulado.application.data.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
