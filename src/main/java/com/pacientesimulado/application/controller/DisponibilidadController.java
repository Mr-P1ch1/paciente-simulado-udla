package com.pacientesimulado.application.controller;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.services.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @Autowired
    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @GetMapping("/actor/{actorId}")
    public List<Disponibilidad> obtenerDisponibilidadesPorActorId(@PathVariable String actorId) {
        return disponibilidadService.obtenerDisponibilidadesPorActorId(actorId);
    }

    @PostMapping("/guardar")
    public Disponibilidad guardarDisponibilidad(@RequestBody Disponibilidad disponibilidad) {
        return disponibilidadService.guardarDisponibilidad(disponibilidad);
    }

    @DeleteMapping("/{id}")
    public void eliminarDisponibilidad(@PathVariable String id) {
        disponibilidadService.eliminarDisponibilidad(id);
    }
}
