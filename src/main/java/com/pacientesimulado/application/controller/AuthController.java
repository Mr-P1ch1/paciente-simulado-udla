package com.pacientesimulado.application.controller;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity< Usuario > login( @RequestBody LoginRequest loginRequest) {
        Usuario usuario = authService.login(loginRequest.getCorreo(), loginRequest.getContraseña());
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

class LoginRequest {
    private String correo;
    private String contraseña;

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
}