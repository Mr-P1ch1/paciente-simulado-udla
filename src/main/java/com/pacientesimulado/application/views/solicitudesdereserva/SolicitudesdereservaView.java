package com.pacientesimulado.application.views.solicitudesdereserva;

import com.pacientesimulado.application.data.*;
import com.pacientesimulado.application.services.*;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Route(value = "solicitudes-de-reserva", layout = MainLayout.class)
@PageTitle("Solicitudes de Reserva")
public class SolicitudesdereservaView extends VerticalLayout {

    private final ReservaService reservaService;
    private final ActorService actorService;
    private final UsuarioService usuarioService;
    private final DoctorService doctorService;
    private final Grid<Reserva> grid;
    private static final Logger LOGGER = Logger.getLogger(SolicitudesdereservaView.class.getName());

    @Autowired

    public SolicitudesdereservaView(ReservaService reservaService, ActorService actorService, UsuarioService usuarioService, DoctorService doctorService) {
        this.reservaService = reservaService;
        this.actorService = actorService;
        this.usuarioService = usuarioService;
        this.doctorService = doctorService;
        this.grid = new Grid<>(Reserva.class, false);

        configureGrid();
        add(grid, createExportButton());

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
        grid.addColumn(Reserva::getCaso).setHeader("Caso").setSortable(true).setAutoWidth(true);

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
        dialog.setWidth("600px");
        dialog.setHeight("auto");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        ComboBox<Actor> actorComboBox = new ComboBox<>("Seleccione Actor");
        actorComboBox.setItemLabelGenerator(actor -> {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
            return (usuario != null) ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
        });

        List<Actor> todosLosActores = actorService.obtenerTodosLosActores();
        actorComboBox.setItems(todosLosActores);

        // Preseleccionar el actor asignado
        if (reserva.getActoresAsignados() != null && !reserva.getActoresAsignados().isEmpty()) {
            Actor actorAsignado = reserva.getActoresAsignados().get(actorIndex - 1);
            actorComboBox.setValue(actorAsignado);
        }

        ComboBox<String> tipoSeccionComboBox = new ComboBox<>("Tipo de Sección");
        tipoSeccionComboBox.setItems("Tipo A", "Tipo B");

        ComboBox<String> generoComboBox = new ComboBox<>("Género del paciente");
        generoComboBox.setItems("Femenino", "Masculino", "No relevante");
        generoComboBox.setWidth("min-content");

        ComboBox<String> edadComboBox = new ComboBox<>("Rango de edad del paciente simulado");
        edadComboBox.setItems("Joven (18-29 años)", "Adulto (30-39 años)", "Adulto medio (40-49 años)", "Adulto mayor (50 años en adelante)", "No relevante");
        edadComboBox.setWidth("260px");

        Paciente paciente = reserva.getPacientes().get(actorIndex - 1);
        generoComboBox.setValue(paciente.getGenero());
        edadComboBox.setValue(paciente.getRangoEdad());
        tipoSeccionComboBox.setValue(reserva.getTipoReserva());

        ComboBox<LocalDate> fechaPracticaComboBox = new ComboBox<>("Fecha de Práctica");
        ComboBox<String> horaPracticaComboBox = new ComboBox<>("Hora de Práctica");

        fechaPracticaComboBox.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            if (selectedDate != null) {
                filterHorasDisponibles(reserva.getCorreoDoctor(), selectedDate, horaPracticaComboBox);
                if (horaPracticaComboBox.getValue() != null) {
                    filterActors(actorComboBox, generoComboBox.getValue(), edadComboBox.getValue(), selectedDate, horaPracticaComboBox.getValue());
                }
            }
        });

        horaPracticaComboBox.addValueChangeListener(event -> {
            if (fechaPracticaComboBox.getValue() != null && event.getValue() != null) {
                filterActors(actorComboBox, generoComboBox.getValue(), edadComboBox.getValue(), fechaPracticaComboBox.getValue(), event.getValue());
            }
        });

        filterFechasYHorasDisponibles(reserva.getCorreoDoctor(), fechaPracticaComboBox, horaPracticaComboBox);

        TextField aulaTextField = new TextField("Aula");
        aulaTextField.setValue(reserva.getAula() != null ? reserva.getAula() : "");

        if (reserva.getFechaEntrenamiento() == null || reserva.getHorasEntrenamiento() == null || reserva.getHorasEntrenamiento().isEmpty()) {
            dialogLayout.add(new HorizontalLayout(new Span("Fecha de Práctica: "), fechaPracticaComboBox));
            dialogLayout.add(new HorizontalLayout(new Span("Hora de Práctica: "), horaPracticaComboBox));
        } else {
            dialogLayout.add(
                    new Span("Fecha de Práctica: " + reserva.getFechaEntrenamiento()),
                    new Span("Hora de Práctica: " + String.join(", ", reserva.getHorasEntrenamiento()))
            );
        }

        Button assignButton = new Button("Asignar");
        assignButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        assignButton.addClickListener(event -> {
            if (actorComboBox.getValue() != null) {
                Actor actorSeleccionado = actorComboBox.getValue();
                String tipoSeccion = tipoSeccionComboBox.getValue();
                LocalDate fechaPractica = fechaPracticaComboBox.getValue();
                String horaPractica = horaPracticaComboBox.getValue();

                SesionAsignada sesionAsignada = new SesionAsignada();
                sesionAsignada.setIdReserva(reserva.getId());
                sesionAsignada.setTipoSeccion(tipoSeccion);
                actorSeleccionado.getSesionesAsignadas().add(sesionAsignada);

                actualizarDisponibilidadActor(actorSeleccionado, reserva, horaPractica);
                actualizarDisponibilidadDoctor(reserva.getCorreoDoctor(), actorSeleccionado, reserva, horaPractica);

                actorService.guardarActor(actorSeleccionado);
                reserva.setTipoReserva(tipoSeccion);
                reserva.setFechaEntrenamiento(fechaPractica);
                reserva.setHorasEntrenamiento(Collections.singletonList(horaPractica));
                reserva.setAula(aulaTextField.getValue());
                reservaService.asignarActor(reserva, actorSeleccionado);

                Notification.show("Actor " + actorIndex + " asignado correctamente.");
                dialog.close();
                listReservas();
            } else {
                Notification.show("Por favor, seleccione un actor.");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        dialogLayout.add(
                new Span("Estado: " + reserva.getEstado()),
                new Span("Tipo de Sección: " + (reserva.getTipoReserva() != null ? reserva.getTipoReserva() : "No asignada")),
                new Span("Doctor: " + (usuarioService.obtenerUsuarioPorCorreo(reserva.getCorreoDoctor()).map(doc -> doc.getNombre() + " " + doc.getApellido()).orElse("Desconocido"))),
                new Span("Correo del Doctor: " + reserva.getCorreoDoctor()),
                new Span("Actividad: " + reserva.getActividad()),
                new Span("Carrera: " + reserva.getCarrera()),
                new Span("Caso: " + reserva.getCaso()),
                new Span("Fecha de Sección: " + (reserva.getFechaSeccion() != null ? reserva.getFechaSeccion() : "No asignada")),
                new Span("Hora de Sección: " + (reserva.getHorasSeccion() != null ? String.join(", ", reserva.getHorasSeccion()) : "No asignada")),
                generoComboBox, edadComboBox, actorComboBox, tipoSeccionComboBox, aulaTextField,
                new HorizontalLayout(assignButton, cancelButton)
        );

        dialog.add(dialogLayout);
        dialog.open();
    }



    private void filterFechasYHorasDisponibles(String correoDoctor, ComboBox<LocalDate> fechaPracticaComboBox, ComboBox<String> horaPracticaComboBox) {
        Optional<Doctor> doctorOptional = doctorService.obtenerDoctorPorCorreo(correoDoctor);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            List<Disponibilidad> disponibilidades = doctor.getDisponibilidades();

            Set<LocalDate> fechasDisponibles = disponibilidades.stream()
                    .map(Disponibilidad::getFecha)
                    .collect(Collectors.toSet());
            fechaPracticaComboBox.setItems(fechasDisponibles);

            fechaPracticaComboBox.addValueChangeListener(event -> {
                LocalDate selectedDate = event.getValue();
                if (selectedDate != null) {
                    List<String> horasDisponibles = disponibilidades.stream()
                            .filter(disponibilidad -> disponibilidad.getFecha().equals(selectedDate))
                            .flatMap(disponibilidad -> disponibilidad.getHoras().stream())
                            .filter(horaDisponibilidad -> horaDisponibilidad.getEstado().equals("libre"))
                            .map(Disponibilidad.HoraDisponibilidad::getHora)
                            .collect(Collectors.toList());
                    horaPracticaComboBox.setItems(horasDisponibles);
                }
            });
        }
    }

    private void filterHorasDisponibles(String correoDoctor, LocalDate selectedDate, ComboBox<String> horaPracticaComboBox) {
        Optional<Doctor> doctorOptional = doctorService.obtenerDoctorPorCorreo(correoDoctor);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            List<String> horasDisponibles = doctor.getDisponibilidades().stream()
                    .filter(disponibilidad -> disponibilidad.getFecha().equals(selectedDate))
                    .flatMap(disponibilidad -> disponibilidad.getHoras().stream())
                    .filter(horaDisponibilidad -> horaDisponibilidad.getEstado().equals("libre"))
                    .map(Disponibilidad.HoraDisponibilidad::getHora)
                    .collect(Collectors.toList());
            horaPracticaComboBox.setItems(horasDisponibles);
        }
    }

    private void actualizarDisponibilidadActor(Actor actor, Reserva reserva, String horaSeleccionada) {
        String nombreDoctor = usuarioService.obtenerUsuarioPorCorreo(reserva.getCorreoDoctor())
                .map(doc -> doc.getNombre() + " " + doc.getApellido())
                .orElse("Desconocido");

        actor.getDisponibilidades().forEach(disponibilidad -> {
            if (disponibilidad.getFecha().equals(reserva.getFechaEntrenamiento())) {
                disponibilidad.getHoras().forEach(horaDisponibilidad -> {
                    if (horaDisponibilidad.getHora().equals(horaSeleccionada)) {
                        horaDisponibilidad.setEstado("Entrenamiento con " + nombreDoctor + " para la sección " + reserva.getCaso());
                    }
                });
            }
        });
    }

    private void actualizarDisponibilidadDoctor(String correoDoctor, Actor actor, Reserva reserva, String horaSeleccionada) {
        Optional<Doctor> doctorOptional = doctorService.obtenerDoctorPorCorreo(correoDoctor);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            String nombreActor = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo())
                    .map(act -> act.getNombre() + " " + act.getApellido())
                    .orElse("Desconocido");

            doctor.getDisponibilidades().forEach(disponibilidad -> {
                if (disponibilidad.getFecha().equals(reserva.getFechaEntrenamiento())) {
                    disponibilidad.getHoras().forEach(horaDisponibilidad -> {
                        if (horaDisponibilidad.getHora().equals(horaSeleccionada)) {
                            horaDisponibilidad.setEstado("Entrenamiento con " + nombreActor + " para la sección " + reserva.getCaso());
                        }
                    });
                }
            });
            doctorService.guardarDoctor(doctor);
        }
    }

    private void filterActors(ComboBox<Actor> actorComboBox, String genero, String edad, LocalDate fechaEntrenamiento, String horaEntrenamiento) {
        List<Actor> todosLosActores = actorService.obtenerTodosLosActores();

        List<Actor> actoresFiltrados = todosLosActores.stream()
                .filter(actor -> {
                    boolean generoCoincide = genero.equals("No relevante") || genero.equals(actor.getSexo());
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

                    boolean disponibilidadCoincide = actor.getDisponibilidades().stream()
                            .anyMatch(disponibilidad -> disponibilidad.getFecha().equals(fechaEntrenamiento) &&
                                    disponibilidad.getHoras().stream()
                                            .anyMatch(horaDisponibilidad -> horaDisponibilidad.getHora().equals(horaEntrenamiento) &&
                                                    horaDisponibilidad.getEstado().equals("libre")));

                    return generoCoincide && edadCoincide && disponibilidadCoincide;
                })
                .collect(Collectors.toList());

        actorComboBox.setItems(actoresFiltrados);
        actorComboBox.setItemLabelGenerator(actor -> {
            Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(actor.getCorreo()).orElse(null);
            return (usuario != null) ? usuario.getNombre() + " " + usuario.getApellido() : "Sin nombre";
        });
    }

    private Button createExportButton() {
        Button exportButton = new Button("Exportar a Excel");
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.addClickListener(event -> {
            StreamResource resource = new StreamResource("reservas.xlsx", this::exportToExcel);
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            downloadLink.getElement().callJsFunction("click");
        });
        return exportButton;
    }

    private ByteArrayInputStream exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reservas");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Estado");
            headerRow.createCell(1).setCellValue("Tipo de Sección");
            headerRow.createCell(2).setCellValue("Doctor");
            headerRow.createCell(3).setCellValue("Actividad");
            headerRow.createCell(4).setCellValue("Caso");
            headerRow.createCell(5).setCellValue("Fecha de Entrenamiento");
            headerRow.createCell(6).setCellValue("Horas de Entrenamiento");
            headerRow.createCell(7).setCellValue("Aula");
            headerRow.createCell(8).setCellValue("Correo del Doctor");
            headerRow.createCell(9).setCellValue("Carrera");
            headerRow.createCell(10).setCellValue("Tipo");
            headerRow.createCell(11).setCellValue("Fecha de Sección");
            headerRow.createCell(12).setCellValue("Horas de Sección");
            headerRow.createCell(13).setCellValue("Pacientes");
            headerRow.createCell(14).setCellValue("Actores Asignados");
            headerRow.createCell(15).setCellValue("Forma de Requerimiento");

            List<Reserva> reservas = reservaService.obtenerTodasLasReservas();
            int rowIdx = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(reserva.getEstado());
                row.createCell(1).setCellValue(reserva.getTipoReserva());
                Usuario doctor = usuarioService.obtenerUsuarioPorCorreo(reserva.getCorreoDoctor()).orElse(null);
                row.createCell(2).setCellValue(doctor != null ? doctor.getNombre() + " " + doctor.getApellido() : reserva.getCorreoDoctor());
                row.createCell(3).setCellValue(reserva.getActividad());
                row.createCell(4).setCellValue(reserva.getCaso());
                row.createCell(5).setCellValue(reserva.getFechaEntrenamiento() != null ? reserva.getFechaEntrenamiento().toString() : "");
                row.createCell(6).setCellValue(reserva.getHorasEntrenamiento() != null ? String.join(", ", reserva.getHorasEntrenamiento()) : "");
                row.createCell(7).setCellValue(reserva.getAula());
                row.createCell(8).setCellValue(reserva.getCorreoDoctor());
                row.createCell(9).setCellValue(reserva.getCarrera());
                row.createCell(10).setCellValue(reserva.getTipo());
                row.createCell(11).setCellValue(reserva.getFechaSeccion() != null ? reserva.getFechaSeccion().toString() : "");
                row.createCell(12).setCellValue(reserva.getHorasSeccion() != null ? String.join(", ", reserva.getHorasSeccion()) : "");
                row.createCell(13).setCellValue(reserva.getPacientes() != null ? reserva.getPacientes().stream().map(Paciente::getGenero).collect(Collectors.joining(", ")) : "");
                row.createCell(14).setCellValue(reserva.getActoresAsignados() != null ? reserva.getActoresAsignados().stream().map(Actor::getNombre).collect(Collectors.joining(", ")) : "");
                row.createCell(15).setCellValue(reserva.getFormaRequerimiento());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            LOGGER.severe("Error al exportar las reservas a Excel: " + e.getMessage());
            return null;
        }
    }
}
