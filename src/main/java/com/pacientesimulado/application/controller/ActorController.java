package com.pacientesimulado.application.controller;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.services.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actores")
public class ActorController {
    @Autowired
    private ActorService actorService;

    @PostMapping("/registro")
    public ResponseEntity<Actor> registrarActor(@RequestBody Actor actor) {
        Actor nuevoActor = actorService.registrarActor(actor);
        return ResponseEntity.ok(nuevoActor);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Actor> actualizarActor(@PathVariable Long id, @RequestBody Actor actor) {
        Actor actorActualizado = actorService.actualizarActor(id, actor);
        return ResponseEntity.ok(actorActualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> obtenerActor(@PathVariable Long id) {
        Actor actor = actorService.obtenerActor(id);
        return ResponseEntity.ok(actor);
    }
}
