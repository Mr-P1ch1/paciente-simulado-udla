package com.pacientesimulado.application.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "disponibilidad")
public class Disponibilidad {
    @Id
    private String id;
    private String actorId;
    private LocalDate fechaInicioSemana;
    private Map<String, Boolean> disponibilidad;
    private Map<String, String[]> horasDisponibles;

    public Disponibilidad() {
        this.disponibilidad = new HashMap<>();
        this.horasDisponibles = new HashMap<>();
    }

    public Disponibilidad(LocalDate fechaInicioSemana) {
        this();
        this.fechaInicioSemana = fechaInicioSemana;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public LocalDate getFechaInicioSemana() {
        return fechaInicioSemana;
    }

    public void setFechaInicioSemana(LocalDate fechaInicioSemana) {
        this.fechaInicioSemana = fechaInicioSemana;
    }

    public Boolean isDisponible(String dia) {
        return disponibilidad.getOrDefault(dia, false);
    }

    public void setDisponible(String dia, Boolean disponible) {
        disponibilidad.put(dia, disponible);
    }

    public String[] getHorasDisponibles(String dia) {
        return horasDisponibles.getOrDefault(dia, new String[]{});
    }

    public void setHorasDisponibles(String dia, String[] horas) {
        horasDisponibles.put(dia, horas);
    }
}
