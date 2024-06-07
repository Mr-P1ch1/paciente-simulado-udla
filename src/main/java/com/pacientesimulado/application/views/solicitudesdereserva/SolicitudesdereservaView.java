package com.pacientesimulado.application.views.solicitudesdereserva;

import com.pacientesimulado.application.data.*;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.ReservaService;
import com.pacientesimulado.application.services.UsuarioService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Route(value = "solicitudes-de-reserva", layout = MainLayout.class)
@PageTitle("Solicitudes de Reserva")
public class SolicitudesdereservaView extends VerticalLayout {

    private final ReservaService reservaService;
    private final ActorService actorService;
    private final UsuarioService usuarioService;
    private final Grid<Reserva> grid;
    private static final Logger LOGGER = Logger.getLogger(SolicitudesdereservaView.class.getName());

    @Autowired
    public SolicitudesdereservaView(ReservaService reservaService, ActorService actorService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.actorService = actorService;
        this.usuarioService = usuarioService;
        this.grid = new Grid<>(Reserva.class, false);

        configureGrid();
        add(grid);

        listReservas();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addColumn(Reserva::getEstado).setHeader("Estado").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getTipoReserva).setHeader("Tipo de Sección").setSortable(true).setAutoWidth(true);
        grid.addColumn(reserva -> {
            Usuario doctor = usuarioService.obtenerUsuarioPorCorreo(reserva.getCorreoDoctor()).orElse(null);
            return doctor != null ? doctor.getNombre() + " " + doctor.getApellido() : reserva.getCorreoDoctor();
        }).setHeader("Doctor").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getActividad).setHeader("Actividad").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getCarrera).setHeader("Carrera").setSortable(true).setAutoWidth(true);
        grid.addColumn(Reserva::getCaso).setHeader("Caso").setSortable(true).setAutoWidth(true);
        grid.addColumn(reserva -> {
            LocalDate fechaSeccion = reserva.getFechaSeccion();
            return (fechaSeccion != null) ? fechaSeccion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        }).setHeader("Fecha de Sección").setSortable(true).setAutoWidth(true);
        grid.addColumn(reserva -> {
            List<Actor> actoresAsignados = reserva.getActoresAsignados();
            return (actoresAsignados != null && !actoresAsignados.isEmpty()) ?
                    actoresAsignados.stream()
                            .map(actor -> {
                                Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
                                return usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
                            })
                            .collect(Collectors.joining(", ")) : "No asignado";
        }).setHeader("Actor Asignado").setSortable(true).setAutoWidth(true);

        grid.addItemClickListener(event -> showAssignActorDialog(event.getItem(), 1));
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

    private void showAssignActorDialog(Reserva reserva, int actorIndex) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        ComboBox<Actor> actorComboBox = new ComboBox<>("Seleccione Actor");
        actorComboBox.setItemLabelGenerator(actor -> {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
            return (usuario != null) ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
        });

        ComboBox<String> tipoSeccionComboBox = new ComboBox<>("Tipo de Sección");
        tipoSeccionComboBox.setItems("Tipo A", "Tipo B");

        ComboBox<String> generoComboBox = new ComboBox<>("Género del paciente");
        generoComboBox.setItems("Femenino", "Masculino", "No relevante");
        generoComboBox.setWidth("min-content");

        ComboBox<String> edadComboBox = new ComboBox<>("Rango de edad del paciente simulado");
        edadComboBox.setItems("Joven (18-29 años)", "Adulto (30-39 años)", "Adulto medio (40-49 años)", "Adulto mayor (50 años en adelante)", "No relevante");
        edadComboBox.setWidth("260px");

        // Pre-fill the ComboBoxes with reservation data
        Paciente paciente = reserva.getPacientes().get(actorIndex - 1);
        generoComboBox.setValue(paciente.getGenero());
        edadComboBox.setValue(paciente.getRangoEdad());
        tipoSeccionComboBox.setValue(reserva.getTipoReserva());

        filterActors(actorComboBox, generoComboBox.getValue(), edadComboBox.getValue(), reserva);

        // Inicializa la lista de actores asignados si es nula
        if (reserva.getActoresAsignados() == null) {
            reserva.setActoresAsignados(new ArrayList <> ());
        }

        // Preselect assigned actor if available
        Optional<Actor> actorAsignado = reserva.getActoresAsignados().stream().findFirst();
        actorAsignado.ifPresent(actor -> actorComboBox.setValue(actor));

        // Allow changing ComboBoxes only if no actors are available
        if (actorComboBox.isEmpty()) {
            generoComboBox.setEnabled(true);
            edadComboBox.setEnabled(true);
        } else {
            generoComboBox.setEnabled(false);
            edadComboBox.setEnabled(false);
        }

        Button assignButton = new Button("Asignar");
        assignButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        assignButton.addClickListener(event -> {
            if (actorComboBox.getValue() != null) {
                // Asignar el actor seleccionado a la reserva y actualizar el estado
                Actor actorSeleccionado = actorComboBox.getValue();
                String tipoSeccion = tipoSeccionComboBox.getValue();

                SesionAsignada sesionAsignada = new SesionAsignada();
                sesionAsignada.setIdReserva(reserva.getId());
                sesionAsignada.setTipoSeccion(tipoSeccion);
                actorSeleccionado.getSesionesAsignadas().add(sesionAsignada);

                actualizarDisponibilidadActor(actorSeleccionado, reserva);

                actorService.guardarActor(actorSeleccionado);
                reserva.setTipoReserva(tipoSeccion); // Guardar el tipo de sección seleccionado en la reserva
                reservaService.asignarActor(reserva, actorSeleccionado);
                Notification.show("Actor " + actorIndex + " asignado correctamente.");
                dialog.close();
                listReservas();
            } else {
                Notification.show("Por favor, seleccione un actor.");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        dialogLayout.add(new Span("Asignar Actor a la Reserva"),
                new Span("Fecha de Práctica: " + reserva.getFechaEntrenamiento()),
                new Span("Hora de Práctica: " + String.join(", ", reserva.getHorasEntrenamiento())),
                new Span("Fecha de Sección: " + reserva.getFechaSeccion()),
                new Span("Hora de Sección: " + String.join(", ", reserva.getHorasSeccion())),
                generoComboBox, edadComboBox, actorComboBox, tipoSeccionComboBox, new HorizontalLayout(assignButton, cancelButton));

        if (actorComboBox.isEmpty()) {
            dialogLayout.add(new Span("No hay disponibilidad de actores con las características seleccionadas."));
        }

        dialog.add(dialogLayout);
        dialog.open();
    }


    private void filterActors(ComboBox<Actor> actorComboBox, String genero, String edad, Reserva reserva) {
        List<Actor> todosLosActores = actorService.obtenerTodosLosActores();

        // Log todos los actores obtenidos
        System.out.println("Todos los actores obtenidos: ");
        todosLosActores.forEach(actor -> {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
            String nombreCompleto = (usuario != null) ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
            System.out.println("Actor: " + nombreCompleto);
            actor.getDisponibilidades().forEach(disponibilidad -> {
                System.out.println("Disponibilidad Fecha: " + disponibilidad.getFecha());
                disponibilidad.getHoras().forEach(horaDisponibilidad ->
                        System.out.println("Hora: " + horaDisponibilidad.getHora() + " Estado: " + horaDisponibilidad.getEstado()));
            });
        });

        List<Actor> actoresFiltrados = todosLosActores.stream()
                .filter(actor -> {
                    boolean generoCoincide = genero.equals("No relevante") || actor.getSexo().equals(genero);
                    boolean edadCoincide = false;

                    if (edad.equals("No relevante")) {
                        edadCoincide = true;
                    } else {
                        switch (edad) {
                            case "Joven (18-29 años)":
                                edadCoincide = actor.getEdad() >= 18 && actor.getEdad() <= 29;
                                break;
                            case "Adulto (30-39 años)":
                                edadCoincide = actor.getEdad() >= 30 && actor.getEdad() <= 39;
                                break;
                            case "Adulto medio (40-49 años)":
                                edadCoincide = actor.getEdad() >= 40 && actor.getEdad() <= 49;
                                break;
                            case "Adulto mayor (50 años en adelante)":
                                edadCoincide = actor.getEdad() >= 50;
                                break;
                        }
                    }

                    boolean fechaCoincide = actor.getDisponibilidades().stream()
                            .anyMatch(disponibilidad -> {
                                boolean fechaMatch = disponibilidad.getFecha().equals(reserva.getFechaSeccion());
                                System.out.println("Comparando fechas: " + disponibilidad.getFecha() + " con " + reserva.getFechaSeccion() + " -> " + fechaMatch);
                                return fechaMatch;
                            });

                    boolean horasCoinciden = actor.getDisponibilidades().stream()
                            .filter(disponibilidad -> disponibilidad.getFecha().equals(reserva.getFechaSeccion()))
                            .anyMatch(disponibilidad -> {
                                boolean horaMatch = disponibilidad.getHoras().stream()
                                        .anyMatch(horaDisponibilidad -> {
                                            boolean estadoLibre = horaDisponibilidad.getEstado().equals("libre");
                                            boolean horaContenida = Arrays.asList(reserva.getHorasSeccion()).contains(horaDisponibilidad.getHora());
                                            System.out.println("Comparando hora: " + horaDisponibilidad.getHora() + " Estado: " + horaDisponibilidad.getEstado() + " -> Estado libre: " + estadoLibre + ", Hora contenida: " + horaContenida);
                                            return estadoLibre && horaContenida;
                                        });
                                return horaMatch;
                            });

                    System.out.println("Genero coincide: " + generoCoincide);
                    System.out.println("Edad coincide: " + edadCoincide);
                    System.out.println("Fecha coincide: " + fechaCoincide);
                    System.out.println("Horas coinciden: " + horasCoinciden);

                    return generoCoincide && edadCoincide && fechaCoincide && horasCoinciden;
                })
                .collect(Collectors.toList());

        actorComboBox.setItems(actoresFiltrados);
        actorComboBox.setItemLabelGenerator(actor -> {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
            return (usuario != null) ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
        });

        // Log para depuración
        System.out.println("Todos los actores: " + todosLosActores);
        System.out.println("Actores filtrados: " + actoresFiltrados);
    }

    private void actualizarDisponibilidadActor(Actor actor, Reserva reserva) {
        for (String horaSeccion : reserva.getHorasSeccion()) {
            actor.getDisponibilidades().forEach(disponibilidad -> {
                if (disponibilidad.getFecha().equals(reserva.getFechaSeccion())) {
                    disponibilidad.getHoras().forEach(horaDisponibilidad -> {
                        if (horaDisponibilidad.getHora().equals(horaSeccion)) {
                            horaDisponibilidad.setEstado(reserva.getCaso());
                        }
                    });
                }
            });
        }
    }
}
