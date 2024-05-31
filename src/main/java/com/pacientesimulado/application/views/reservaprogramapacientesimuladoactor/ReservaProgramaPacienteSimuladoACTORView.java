package com.pacientesimulado.application.views.reservaprogramapacientesimuladoactor;

import com.pacientesimulado.application.data.Materia;
import com.pacientesimulado.application.data.Paciente;
import com.pacientesimulado.application.data.Reserva;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.MateriaService;
import com.pacientesimulado.application.services.ReservaService;
import com.pacientesimulado.application.services.UsuarioService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Reserva Programa Paciente Simulado (ACTOR)")
@Route(value = "reserva-programa-paciente-simulado-actor", layout = MainLayout.class)
@AnonymousAllowed
public class ReservaProgramaPacienteSimuladoACTORView extends Composite<VerticalLayout> {

    private final UsuarioService usuarioService;
    private final MateriaService materiaService;
    private final ReservaService reservaService;
    private EmailField emailField;
    private Button validateButton;
    private VerticalLayout mainLayout;
    private LocalDate fechaSeleccionada;
    private DatePicker datePicker;
    private Button colocarDisponibilidadButton;
    private Map<String, Map<String, Checkbox>> disponibilidadMap;
    private ComboBox<String> comboBoxActividad;
    private ComboBox<Integer> comboBoxNumeroPacientes;
    private ComboBox<String> comboBoxRequerimiento;
    private DatePicker fechaEntrenamiento;
    private ComboBox<String> horarioEntrenamiento;

    @Autowired
    public ReservaProgramaPacienteSimuladoACTORView(UsuarioService usuarioService, MateriaService materiaService, ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.materiaService = materiaService;
        this.reservaService = reservaService;

        emailField = new EmailField("Correo electrónico");
        emailField.setWidth("300px");
        validateButton = new Button("Validar");
        validateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");

        validateButton.addClickListener(event -> validateDoctorEmail());

        getContent().add(new Paragraph("Ingrese su correo para continuar:"));
        getContent().add(emailField, validateButton, mainLayout);
    }

    private void validateDoctorEmail() {
        String email = emailField.getValue();
        Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(email);

        if (usuarioOptional.isPresent() && "Doctor".equalsIgnoreCase(usuarioOptional.get().getRol())) {
            showWelcomeMessage(usuarioOptional.get().getNombre());
            showForm(email);
        } else {
            Notification.show("Acceso denegado. Solo los doctores pueden acceder a esta vista.");
        }
    }

    private void showWelcomeMessage(String doctorName) {
        Notification.show("Bienvenido, " + doctorName);
    }

    private void showForm(String correoDoctor) {
        mainLayout.removeAll();

        Paragraph textSmall = new Paragraph("Si necesita consultar el caso que requiere antes de llenar el formulario por favor de click en el siguiente Link para que pueda revisarlo.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");

        Anchor link = new Anchor("https://udlaec-my.sharepoint.com/:f:/g/personal/rocio_paredes_udla_edu_ec/EojckZ4-1wdIhAYnTu6HYf4B-BJwp_aYFdc_p58HG11-Qw?e=efvbdR", "Consultar casos");
        link.setWidth("100%");

        ComboBox<String> carreraComboBox = new ComboBox<>("Seleccione la carrera");
        ComboBox<String> tipoComboBox = new ComboBox<>("Seleccione el tipo");
        ComboBox<String> casoComboBox = new ComboBox<>("Seleccione el caso");
        Button buttonPrimary = new Button("Siguiente");

        carreraComboBox.setItems(materiaService.obtenerTodasLasCarreras());

        carreraComboBox.addValueChangeListener(event -> {
            String carreraSeleccionada = event.getValue();
            if (carreraSeleccionada != null) {
                tipoComboBox.clear();
                casoComboBox.clear();
                mainLayout.removeAll();
                mainLayout.add(textSmall, link, carreraComboBox, tipoComboBox, casoComboBox);

                List<Materia> materias = materiaService.obtenerMateriasPorCarrera(carreraSeleccionada);
                List<String> tipos = materias.stream()
                        .flatMap(m -> m.getTiposYCasos().keySet().stream())
                        .distinct()
                        .collect(Collectors.toList());
                tipoComboBox.setItems(tipos);

                tipoComboBox.addValueChangeListener(tipoEvent -> {
                    String tipoSeleccionado = tipoEvent.getValue();
                    if (tipoSeleccionado != null) {
                        List<String> casos = materias.stream()
                                .filter(m -> m.getTiposYCasos().containsKey(tipoSeleccionado))
                                .flatMap(m -> m.getTiposYCasos().get(tipoSeleccionado).stream())
                                .distinct()
                                .collect(Collectors.toList());
                        casoComboBox.setItems(casos);
                    }
                });

                buttonPrimary.addClickListener(primaryEvent -> {
                    if (carreraComboBox.getValue() != null && tipoComboBox.getValue() != null && casoComboBox.getValue() != null) {
                        mostrarFormularioDisponibilidad(carreraComboBox.getValue(), tipoComboBox.getValue(), casoComboBox.getValue(), correoDoctor);
                    } else {
                        Notification.show("Por favor, complete todos los campos antes de continuar.");
                    }
                });

                mainLayout.add(buttonPrimary);
            }
        });

        mainLayout.add(carreraComboBox);
    }

    private void mostrarFormularioDisponibilidad(String carrera, String tipo, String caso, String correoDoctor) {
        mainLayout.removeAll();

        datePicker = new DatePicker("Fecha de inicio de semana");
        datePicker.setI18n(new DatePicker.DatePickerI18n()
                .setWeekdays(Arrays.asList("domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"))
                .setFirstDayOfWeek(1));

        datePicker.setPlaceholder("Selecciona un lunes");
        datePicker.setEnabled(true);
        datePicker.addValueChangeListener(event -> {
            fechaSeleccionada = event.getValue();
            if (fechaSeleccionada != null) {
                colocarDisponibilidadButton.setEnabled(true);
            } else {
                colocarDisponibilidadButton.setEnabled(false);
            }
        });

        disponibilidadMap = new HashMap<>();
        Grid<String> grid = new Grid<>(String.class, false);
        grid.addColumn(String::toString).setHeader("Hora").setAutoWidth(true);

        String[] horas = {"7h00", "8h05", "9h10", "10h15", "11h20", "12h25", "13h30", "14h35", "15h40", "16h45", "17h50"};
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        for (String dia : dias) {
            Map<String, Checkbox> dayMap = new HashMap<>();
            grid.addComponentColumn(hora -> {
                HorizontalLayout innerLayout = new HorizontalLayout();
                innerLayout.setSpacing(true);
                innerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                Checkbox checkbox = new Checkbox();
                dayMap.put(hora, checkbox);

                innerLayout.add(checkbox);
                return innerLayout;
            }).setHeader(dia).setAutoWidth(true);
            disponibilidadMap.put(dia, dayMap);
        }

        grid.setItems(horas);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        colocarDisponibilidadButton = new Button("Coloca tu disponibilidad");
        colocarDisponibilidadButton.setEnabled(false);
        colocarDisponibilidadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        colocarDisponibilidadButton.addClickListener(event -> {
            mostrarFormularioPacientes(carrera, tipo, caso, correoDoctor);
        });

        mainLayout.add(datePicker, grid, colocarDisponibilidadButton);
    }

    private void mostrarFormularioPacientes(String carrera, String tipo, String caso, String correoDoctor) {
        mainLayout.removeAll();

        comboBoxActividad = new ComboBox<>("Seleccione para que actividad necesita");
        comboBoxActividad.setItems("Clase", "Evaluación", "Examen complexivo", "Capacitación", "Talleres");
        comboBoxActividad.setWidth("270px");

        comboBoxNumeroPacientes = new ComboBox<>("Número de pacientes");
        comboBoxNumeroPacientes.setItems(1, 2, 3, 4, 5, 6);
        comboBoxNumeroPacientes.setWidth("min-content");

        Button siguienteButton = new Button("Siguiente");
        siguienteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        siguienteButton.addClickListener(event -> {
            Integer numberOfPatients = comboBoxNumeroPacientes.getValue();
            if (numberOfPatients != null) {
                updatePatientFields(numberOfPatients, correoDoctor, carrera, tipo, caso);
            } else {
                Notification.show("Por favor, seleccione el número de pacientes.");
            }
        });

        mainLayout.add(comboBoxActividad, comboBoxNumeroPacientes, siguienteButton);
    }

    private void updatePatientFields(int numberOfPatients, String correoDoctor, String carrera, String tipo, String caso) {
        mainLayout.removeAll();
        mainLayout.add(comboBoxActividad, comboBoxNumeroPacientes);

        List<Paciente> pacientes = new ArrayList<>();
        List<Map<String, Object>> patientDataList = new ArrayList<>();

        for (int i = 1; i <= numberOfPatients; i++) {
            VerticalLayout patientLayout = new VerticalLayout();
            patientLayout.add(new com.vaadin.flow.component.html.Hr());
            patientLayout.add(new com.vaadin.flow.component.html.H6("Paciente " + i));

            ComboBox<String> comboBoxGenero = new ComboBox<>("Género del paciente");
            comboBoxGenero.setItems("Femenino", "Masculino", "No relevante");
            comboBoxGenero.setWidth("min-content");

            ComboBox<String> comboBoxEdad = new ComboBox<>("Rango de edad del paciente simulado");
            comboBoxEdad.setItems("Joven (18-29 años)", "Adulto (30-39 años)", "Adulto medio (39-49 años)", "Adulto mayor (50 años en adelante)", "No relevante");
            comboBoxEdad.setWidth("260px");

            ComboBox<String> comboBoxMoulage = new ComboBox<>("¿Requiere moulage?");
            comboBoxMoulage.setItems("Si", "No");
            comboBoxMoulage.setWidth("min-content");

            com.vaadin.flow.component.textfield.TextField textFieldMoulage = new com.vaadin.flow.component.textfield.TextField("Especifique el moulage");
            textFieldMoulage.setWidth("min-content");
            textFieldMoulage.setVisible(false);

            comboBoxMoulage.addValueChangeListener(event -> {
                String selectedMoulage = event.getValue();
                textFieldMoulage.setVisible("Si".equals(selectedMoulage));
            });

            patientLayout.add(comboBoxGenero, comboBoxEdad, comboBoxMoulage, textFieldMoulage);
            mainLayout.add(patientLayout);

            Map<String, Object> patientData = new HashMap<>();
            patientData.put("genero", comboBoxGenero);
            patientData.put("edad", comboBoxEdad);
            patientData.put("moulage", comboBoxMoulage);
            patientData.put("detalleMoulage", textFieldMoulage);
            patientDataList.add(patientData);
        }

        comboBoxRequerimiento = new ComboBox<>("Forma de requerimiento de su Paciente Para la práctica");
        comboBoxRequerimiento.setItems("Presencial", "Virtual");
        fechaEntrenamiento = new DatePicker("Fecha de entrenamiento");
        horarioEntrenamiento = new ComboBox<>("Horario de entrenamiento");
        horarioEntrenamiento.setItems("7h00", "8h05", "9h10", "10h15", "11h20", "12h25", "13h30", "14h35", "15h40", "16h45", "17h50");

        Button guardarButton = new Button("Reservar para su práctica");
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.addClickListener(event -> guardarReserva(pacientes, correoDoctor, carrera, tipo, caso, patientDataList));

        mainLayout.add(comboBoxRequerimiento, fechaEntrenamiento, horarioEntrenamiento, new com.vaadin.flow.component.html.Hr(), guardarButton);
    }

    private void guardarReserva(List<Paciente> pacientes, String correoDoctor, String carrera, String tipo, String caso, List<Map<String, Object>> patientDataList) {
        for (Map<String, Object> patientData : patientDataList) {
            Paciente paciente = new Paciente();
            paciente.setGenero(((ComboBox<String>) patientData.get("genero")).getValue());
            paciente.setRangoEdad(((ComboBox<String>) patientData.get("edad")).getValue());
            paciente.setRequiereMoulage("Si".equals(((ComboBox<String>) patientData.get("moulage")).getValue()));
            paciente.setDetalleMoulage(((com.vaadin.flow.component.textfield.TextField) patientData.get("detalleMoulage")).getValue());

            pacientes.add(paciente);
        }

        Reserva reserva = new Reserva(correoDoctor, carrera, tipo, caso);
        reserva.setActividad(comboBoxActividad.getValue());
        reserva.setNumeroPacientes(comboBoxNumeroPacientes.getValue());
        reserva.setFormaRequerimiento(comboBoxRequerimiento.getValue());
        reserva.setFechaEntrenamiento(fechaEntrenamiento.getValue());
        reserva.setHorasEntrenamiento(new String[]{horarioEntrenamiento.getValue()});
        reserva.setPacientes(pacientes);
        reserva.setEstado("pendiente");
        reserva.setFechaInicioSemana(fechaSeleccionada);

        Map<String, Boolean> disponibilidad = new HashMap<>();
        Map<String, String[]> horasReserva = new HashMap<>();

        for (String dia : disponibilidadMap.keySet()) {
            Map<String, Checkbox> dayMap = disponibilidadMap.get(dia);
            List<String> horas = new ArrayList<>();
            for (String hora : dayMap.keySet()) {
                if (dayMap.get(hora).getValue()) {
                    horas.add(hora);
                }
            }
            if (!horas.isEmpty()) {
                disponibilidad.put(dia, true);
                horasReserva.put(dia, horas.toArray(new String[0]));
            }
        }

        reserva.setDisponible(disponibilidad);
        reserva.setHorasReserva(horasReserva);

        reservaService.guardarReserva(reserva);
        Notification.show("Reserva guardada exitosamente.");

        mainLayout.removeAll();
        showForm(emailField.getValue());

        emailField.clear();
        comboBoxActividad.clear();
        comboBoxNumeroPacientes.clear();
        comboBoxRequerimiento.clear();
        fechaEntrenamiento.clear();
        horarioEntrenamiento.clear();
    }
}
