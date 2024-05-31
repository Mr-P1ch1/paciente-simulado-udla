/*package com.pacientesimulado.application.views.solicitudesdereserva;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.data.Reserva;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.ReservaService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Solicitudes de reserva")
@Route(value = "solicitudes-reserva", layout = MainLayout.class)
@AnonymousAllowed
public class SolicitudesdereservaView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudesdereservaView.class);

    private final ReservaService reservaService;
    private final ActorService actorService;
    private final Grid<Reserva> grid;
    private final Binder <Reserva> binder;

    @Autowired
    public SolicitudesdereservaView(ReservaService reservaService, ActorService actorService) {
        this.reservaService = reservaService;
        this.actorService = actorService;
        this.grid = new Grid<>(Reserva.class);
        this.binder = new Binder<>(Reserva.class);

        logger.info("Inicializando SolicitudesdereservaView");

        configureGrid();
        add(grid);

        listReservas();
    }

    private void configureGrid() {
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        grid.addColumn(Reserva::getCorreoDoctor).setHeader("Correo Doctor");
        grid.addColumn(Reserva::getCarrera).setHeader("Carrera");
        grid.addColumn(Reserva::getTipo).setHeader("Tipo");
        grid.addColumn(Reserva::getCaso).setHeader("Caso");
        grid.addColumn(Reserva::getActividad).setHeader("Actividad");
        grid.addColumn(Reserva::getNumeroPacientes).setHeader("Número de Pacientes");
        grid.addColumn(Reserva::getFormaRequerimiento).setHeader("Forma de Requerimiento");
        grid.addColumn(reserva -> {
            LocalDate fechaEntrenamiento = reserva.getFechaEntrenamiento();
            return (fechaEntrenamiento != null) ? fechaEntrenamiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        }).setHeader("Fecha de Entrenamiento");
        grid.addColumn(reserva -> {
            LocalDate fechaInicioSemana = reserva.getFechaInicioSemana();
            return (fechaInicioSemana != null) ? fechaInicioSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        }).setHeader("Fecha de Inicio de Semana");
        grid.addColumn(reserva -> reserva.getHorasReserva() != null ? reserva.getHorasReserva().toString() : "").setHeader("Horas Reserva");
        grid.addColumn(Reserva::getEstado).setHeader("Estado");

        grid.addComponentColumn(reserva -> {
            Button assignButton = new Button("Asignar Actor");
            assignButton.addClickListener(click -> showAssignDialog(reserva));
            return assignButton;
        }).setHeader("Acciones");

        grid.setHeightFull();
    }

    private void listReservas() {
        try {
            logger.info("Listando reservas");
            List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
            if (reservas != null && !reservas.isEmpty()) {
                grid.setItems(reservas);
                logger.info("Reservas cargadas correctamente: " + reservas.size());
                Notification.show("Reservas cargadas correctamente: " + reservas.size());
            } else {
                Notification.show("No se encontraron reservas.");
                logger.warn("No se encontraron reservas en la base de datos.");
            }
        } catch (Exception e) {
            Notification.show("Error al cargar las reservas.");
            logger.error("Error al cargar las reservas: ", e);
        }
    }

    private void showAssignDialog(Reserva reserva) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        ComboBox<Actor> actorComboBox = new ComboBox<>("Seleccione Actor");
        actorComboBox.setItems(actorService.obtenerTodosLosActores());
        actorComboBox.setItemLabelGenerator(Actor::getNombre);

        Button assignButton = new Button("Asignar");
        assignButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        assignButton.addClickListener(event -> {
            if (actorComboBox.getValue() != null) {
                // Asignar el actor seleccionado a la reserva
                reserva.setEstado("asignado");
                reservaService.guardarReserva(reserva);
                Notification.show("Actor asignado correctamente.");
                dialog.close();
                listReservas();
            } else {
                Notification.show("Por favor, seleccione un actor.");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        formLayout.add(actorComboBox, new HorizontalLayout(assignButton, cancelButton));
        dialog.add(formLayout);
        dialog.open();
    }
}
*/