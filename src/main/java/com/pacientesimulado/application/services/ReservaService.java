package com.pacientesimulado.application.services;

import com.pacientesimulado.application.data.Reserva;
import com.pacientesimulado.application.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> obtenerTodasLasReservas() {
        try {
            List<Reserva> reservas = reservaRepository.findAll();
            logger.info("Reservas obtenidas: " + reservas.size());
            return reservas;
        } catch (Exception e) {
            logger.error("Error al obtener reservas: ", e);
            throw e;
        }
    }

    public void guardarReserva(Reserva reserva) {
        reservaRepository.save(reserva);
    }
}
