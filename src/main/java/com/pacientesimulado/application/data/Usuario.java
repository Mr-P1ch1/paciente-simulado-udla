package com.pacientesimulado.application.data;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol; // doctor, actor, administrador

    public Long getId ( ) {
        return id;
    }

    public void setId ( Long id ) {
        this.id = id;
    }

    public String getNombre ( ) {
        return nombre;
    }

    public void setNombre ( String nombre ) {
        this.nombre = nombre;
    }

    public String getCorreo ( ) {
        return correo;
    }

    public void setCorreo ( String correo ) {
        this.correo = correo;
    }

    public String getContraseña ( ) {
        return contraseña;
    }

    public void setContraseña ( String contraseña ) {
        this.contraseña = contraseña;
    }

    public String getRol ( ) {
        return rol;
    }

    public void setRol ( String rol ) {
        this.rol = rol;
    }
}
