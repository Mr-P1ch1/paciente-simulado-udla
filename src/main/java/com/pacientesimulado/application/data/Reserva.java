package com.pacientesimulado.application.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document(collection = "reservas")
public class Reserva {
    @Id
    private String id;
    private String correoDoctor;
    private String carrera;
    private String tipo;
    private String caso;
    private String actividad;
    private Integer numeroPacientes;
    private String formaRequerimiento;
    private LocalDate fechaEntrenamiento;
    private String[] horasEntrenamiento;
    private LocalDate fechaInicioSemana;
    private Map<String, Boolean> disponible;
    private Map<String, String[]> horasReserva;
    private List<Paciente> pacientes;
    private String estado;
    private List<Actor> actoresAsignados; // Nueva lista para almacenar actores asignados

    // Constructores, getters y setters
    public Reserva(String correoDoctor, String carrera, String tipo, String caso) {
        this.correoDoctor = correoDoctor;
        this.carrera = carrera;
        this.tipo = tipo;
        this.caso = caso;
        this.estado = "pendiente";
    }

    public String getId ( ) {
        return id;
    }

    public void setId ( String id ) {
        this.id = id;
    }

    public String getCorreoDoctor ( ) {
        return correoDoctor;
    }

    public void setCorreoDoctor ( String correoDoctor ) {
        this.correoDoctor = correoDoctor;
    }

    public String getCarrera ( ) {
        return carrera;
    }

    public void setCarrera ( String carrera ) {
        this.carrera = carrera;
    }

    public String getTipo ( ) {
        return tipo;
    }

    public void setTipo ( String tipo ) {
        this.tipo = tipo;
    }

    public String getCaso ( ) {
        return caso;
    }

    public void setCaso ( String caso ) {
        this.caso = caso;
    }

    public String getActividad ( ) {
        return actividad;
    }

    public void setActividad ( String actividad ) {
        this.actividad = actividad;
    }

    public Integer getNumeroPacientes ( ) {
        return numeroPacientes;
    }

    public void setNumeroPacientes ( Integer numeroPacientes ) {
        this.numeroPacientes = numeroPacientes;
    }

    public String getFormaRequerimiento ( ) {
        return formaRequerimiento;
    }

    public void setFormaRequerimiento ( String formaRequerimiento ) {
        this.formaRequerimiento = formaRequerimiento;
    }

    public LocalDate getFechaEntrenamiento ( ) {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento ( LocalDate fechaEntrenamiento ) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public String[] getHorasEntrenamiento ( ) {
        return horasEntrenamiento;
    }

    public void setHorasEntrenamiento ( String[] horasEntrenamiento ) {
        this.horasEntrenamiento = horasEntrenamiento;
    }

    public LocalDate getFechaInicioSemana ( ) {
        return fechaInicioSemana;
    }

    public void setFechaInicioSemana ( LocalDate fechaInicioSemana ) {
        this.fechaInicioSemana = fechaInicioSemana;
    }

    public Map < String, Boolean > getDisponible ( ) {
        return disponible;
    }

    public void setDisponible ( Map < String, Boolean > disponible ) {
        this.disponible = disponible;
    }

    public Map < String, String[] > getHorasReserva ( ) {
        return horasReserva;
    }

    public void setHorasReserva ( Map < String, String[] > horasReserva ) {
        this.horasReserva = horasReserva;
    }

    public List < Paciente > getPacientes ( ) {
        return pacientes;
    }

    public void setPacientes ( List < Paciente > pacientes ) {
        this.pacientes = pacientes;
    }

    public String getEstado ( ) {
        return estado;
    }

    public void setEstado ( String estado ) {
        this.estado = estado;
    }

    public List<Actor> getActoresAsignados() {
        return actoresAsignados;
    }

    public void setActoresAsignados(List<Actor> actoresAsignados) {
        this.actoresAsignados = actoresAsignados;
    }
}
