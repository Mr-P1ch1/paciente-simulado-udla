package com.pacientesimulado.application.data;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidades")
public class Disponibilidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Actor actor;

    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Long getId ( ) {
        return id;
    }

    public void setId ( Long id ) {
        this.id = id;
    }

    public Actor getActor ( ) {
        return actor;
    }

    public void setActor ( Actor actor ) {
        this.actor = actor;
    }

    public DayOfWeek getDia ( ) {
        return dia;
    }

    public void setDia ( DayOfWeek dia ) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio ( ) {
        return horaInicio;
    }

    public void setHoraInicio ( LocalTime horaInicio ) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin ( ) {
        return horaFin;
    }

    public void setHoraFin ( LocalTime horaFin ) {
        this.horaFin = horaFin;
    }
}
