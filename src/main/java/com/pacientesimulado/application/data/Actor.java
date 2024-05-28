package com.pacientesimulado.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "actores")
public class Actor extends Usuario {
    private String sexo;
    private int edad;
    private String talla;
    private String altura;

    @OneToMany(mappedBy = "actor")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "actor")
    private List<Disponibilidad> disponibilidades;

    public String getSexo ( ) {
        return sexo;
    }

    public void setSexo ( String sexo ) {
        this.sexo = sexo;
    }

    public int getEdad ( ) {
        return edad;
    }

    public void setEdad ( int edad ) {
        this.edad = edad;
    }

    public String getTalla ( ) {
        return talla;
    }

    public void setTalla ( String talla ) {
        this.talla = talla;
    }

    public String getAltura ( ) {
        return altura;
    }

    public void setAltura ( String altura ) {
        this.altura = altura;
    }

    public List < Reserva > getReservas ( ) {
        return reservas;
    }

    public void setReservas ( List < Reserva > reservas ) {
        this.reservas = reservas;
    }

    public List < Disponibilidad > getDisponibilidades ( ) {
        return disponibilidades;
    }

    public void setDisponibilidades ( List < Disponibilidad > disponibilidades ) {
        this.disponibilidades = disponibilidades;
    }
}
