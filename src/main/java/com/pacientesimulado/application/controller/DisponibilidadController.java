package com.pacientesimulado.application.controller;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.services.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadController {
    @Autowired
    private DisponibilidadService disponibilidadService;

    @PostMapping("/actualizar/{actorId}")
    public ResponseEntity <List<Disponibilidad>> actualizarDisponibilidad( @PathVariable Long actorId, @RequestBody List<Disponibilidad> disponibilidades) {
        List<Disponibilidad> nuevasDisponibilidades = disponibilidadService.actualizarDisponibilidad(actorId, disponibilidades);
        return ResponseEntity.ok(nuevasDisponibilidades);
    }

    @GetMapping("/{actorId}")
    public ResponseEntity<List<Disponibilidad>> obtenerDisponibilidades(@PathVariable Long actorId) {
        List<Disponibilidad> disponibilidades = disponibilidadService.obtenerDisponibilidades(actorId);
        return ResponseEntity.ok(disponibilidades);
    }
}
