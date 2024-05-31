package com.pacientesimulado.application.controller;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.services.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final DisponibilidadService disponibilidadService;

    @Autowired
    public UsuarioController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @GetMapping("/{actorId}/disponibilidad")
    public List<Disponibilidad> obtenerDisponibilidadPorActorId(@PathVariable String actorId) {
        return disponibilidadService.obtenerPorActorId(actorId);
    }

    @PostMapping("/disponibilidad")
    public Disponibilidad guardarDisponibilidad(@RequestBody Disponibilidad disponibilidad) {
        return disponibilidadService.guardarDisponibilidad(disponibilidad);
    }

    @DeleteMapping("/disponibilidad/{id}")
    public void eliminarDisponibilidad(@PathVariable String id) {
        disponibilidadService.eliminarDisponibilidad(id);
    }
}
