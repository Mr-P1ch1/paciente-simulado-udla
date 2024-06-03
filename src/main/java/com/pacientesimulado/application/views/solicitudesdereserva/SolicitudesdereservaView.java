package com.pacientesimulado.application.views.solicitudesdereserva;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.data.Reserva;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.ReservaService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Solicitudes de reserva")
@Route(value = "solicitudes-reserva", layout = MainLayout.class)
@AnonymousAllowed
public class SolicitudesdereservaView extends VerticalLayout {

    private final ReservaService reservaService;
    private final ActorService actorService;
    private final Grid<Reserva> grid;

    @Autowired
    public SolicitudesdereservaView(ReservaService reservaService, ActorService actorService) {
        this.reservaService = reservaService;
        this.actorService = actorService;
        this.grid = new Grid<>(Reserva.class);

        configureGrid();
        add(grid);

        listReservas();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addColumn(Reserva::getEstado).setHeader("Estado").setSortable(true).setAutoWidth(true);
        grid.addColumn(reserva -> {
            LocalDate fechaSimulacion = reserva.getFechaInicioSemana();
            return (fechaSimulacion != null) ? fechaSimulacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        }).setHeader("Fecha de Práctica").setSortable(true).setAutoWidth(true);
        grid.addColumn(reserva -> {
            LocalDate fechaEntrenamiento = reserva.getFechaEntrenamiento();
            return (fechaEntrenamiento != null) ? fechaEntrenamiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        }).setHeader("Fecha de Capacitación").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getCarrera).setHeader("Carrera").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getCorreoDoctor).setHeader("Correo del Doctor").setSortable(true).setAutoWidth(true);

        grid.addItemClickListener(event -> showDetailsDialog(event.getItem()));
    }

    private void listReservas() {
        List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
        if (reservas != null && !reservas.isEmpty()) {
            grid.setItems(reservas);
            Notification.show("Reservas cargadas correctamente: " + reservas.size());
        } else {
            Notification.show("No se encontraron reservas.");
        }
    }

    private void showDetailsDialog(Reserva reserva) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        Span estadoLabel = new Span("Estado: " + reserva.getEstado());
        Span carreraLabel = new Span("Materia: " + reserva.getCarrera());
        Span tipoLabel = new Span("Tipo: " + reserva.getTipo());
        Span casoLabel = new Span("Caso: " + reserva.getCaso());
        Span actividadLabel = new Span("Actividad: " + reserva.getActividad());
        Span fechaSimulacionLabel = new Span("Fecha de Simulación: " + (reserva.getFechaInicioSemana() != null ? reserva.getFechaInicioSemana().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""));
        Span numeroActoresLabel = new Span("Número de Actores: " + reserva.getNumeroPacientes());
        Span correoDoctorLabel = new Span("Correo Doctor: " + reserva.getCorreoDoctor());

        ComboBox<String> estadoComboBox = new ComboBox<>("Cambiar Estado");
        estadoComboBox.setItems("pendiente", "asignado", "completado");
        estadoComboBox.setValue(reserva.getEstado());

        Button saveEstadoButton = new Button("Guardar Estado");
        saveEstadoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveEstadoButton.addClickListener(event -> {
            reserva.setEstado(estadoComboBox.getValue());
            reservaService.guardarReserva(reserva);
            Notification.show("Estado actualizado a " + estadoComboBox.getValue());
            dialog.close();
            listReservas();
        });

        VerticalLayout actoresLayout = new VerticalLayout();
        actoresLayout.setSpacing(true);
        for (int i = 1; i <= reserva.getNumeroPacientes(); i++) {
            final int actorIndex = i;
            Button asignarActorButton = new Button("Seleccionar Actor " + i);
            asignarActorButton.addClickListener(click -> showAssignActorDialog(reserva, actorIndex));
            actoresLayout.add(asignarActorButton);
        }

        dialogLayout.add(estadoLabel, carreraLabel, tipoLabel, casoLabel, actividadLabel, fechaSimulacionLabel, numeroActoresLabel, correoDoctorLabel, estadoComboBox, saveEstadoButton, actoresLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showAssignActorDialog(Reserva reserva, int actorIndex) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        ComboBox<Actor> actorComboBox = new ComboBox<>("Seleccione Actor");
        actorComboBox.setItems(actorService.obtenerTodosLosActores());
        actorComboBox.setItemLabelGenerator(Actor::getNombre);

        Button assignButton = new Button("Asignar");
        assignButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        assignButton.addClickListener(event -> {
            if (actorComboBox.getValue() != null) {
                // Asignar el actor seleccionado a la reserva y actualizar el estado
                Actor actorSeleccionado = actorComboBox.getValue();
                reservaService.asignarActor(reserva, actorSeleccionado);

                Notification.show("Actor " + actorIndex + " asignado correctamente.");
                dialog.close();
                listReservas();
            } else {
                Notification.show("Por favor, seleccione un actor.");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        dialogLayout.add(new Span("Asignar Actor a la Reserva"), actorComboBox, new HorizontalLayout(assignButton, cancelButton));
        dialog.add(dialogLayout);
        dialog.open();
    }
}
