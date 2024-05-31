package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.DisponibilidadService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

@PageTitle("Disponibilidad semana de Clases")
@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
public class DisponibilidadsemanadeClasesView extends Composite<VerticalLayout> {

    private final DisponibilidadService disponibilidadService;
    private final ActorService actorService;
    private Optional<Actor> actor = Optional.empty();
    private LocalDate fechaSeleccionada;
    private DatePicker datePicker;
    private Button colocarDisponibilidadButton;

    @Autowired
    public DisponibilidadsemanadeClasesView(DisponibilidadService disponibilidadService, ActorService actorService) {
        this.disponibilidadService = disponibilidadService;
        this.actorService = actorService;

        VerticalLayout mainLayout = getContent();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(true);

        EmailField emailField = new EmailField("Correo del actor");
        emailField.setPlaceholder("Ingrese el correo del actor");

        Button ingresarButton = new Button("Ingresar");
        ingresarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ingresarButton.addClickListener(e -> {
            String email = emailField.getValue();
            actorService.obtenerActorPorCorreo(email).ifPresentOrElse(
                    foundActor -> {
                        actor = Optional.of(foundActor);
                        Notification.show("Bienvenido, " + foundActor.getNombre() + ". Selecciona tu disponibilidad.");
                        datePicker.setEnabled(true);
                    },
                    () -> {
                        Notification.show("No se encontró actor con ese correo");
                        actor = Optional.empty();  // Asegurarse de que la variable actor esté vacía si no se encuentra
                    }
            );
        });

        datePicker = new DatePicker("Fecha de inicio de semana");
        datePicker.setI18n(new DatePickerI18n()
                .setWeekdays(Arrays.asList("domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"))
                .setFirstDayOfWeek(1));

        datePicker.setPlaceholder("Selecciona un lunes");
        datePicker.setEnabled(false);
        datePicker.addValueChangeListener(event -> {
            fechaSeleccionada = event.getValue();
            colocarDisponibilidadButton.setText("Coloca tu disponibilidad");
        });

        VerticalLayout mainGrid = new VerticalLayout();
        mainGrid.setWidthFull();
        mainGrid.setSpacing(true);

        Grid<String> grid = new Grid<>(String.class, false);
        grid.addColumn(String::toString).setHeader("Hora").setAutoWidth(true);

        String[] horas = {"7h00", "8h05", "9h10", "10h15", "11h20", "12h25", "13h30", "14h35", "15h40", "16h45", "17h45", "18h50", "19h50"};
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        Map<String, Map<String, Checkbox>> disponibilidadMap = new HashMap<>();

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

        colocarDisponibilidadButton = new Button("Guardar Disponibilidad");
        colocarDisponibilidadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        colocarDisponibilidadButton.addClickListener(e -> {
            if (actor.isPresent() && fechaSeleccionada != null) {
                guardarDisponibilidad(actor.get(), disponibilidadMap);
                Notification.show("Disponibilidad guardada correctamente.");
            } else {
                Notification.show("Por favor, busque un actor y seleccione una fecha primero.");
            }
        });

        mainLayout.setWidth("100%");
        mainLayout.getStyle().set("flex-grow", "1");
        mainLayout.addClassName(Gap.MEDIUM);
        mainLayout.add(emailField, ingresarButton, datePicker, mainGrid, grid, colocarDisponibilidadButton);
        mainLayout.setAlignSelf(FlexComponent.Alignment.END, colocarDisponibilidadButton);
    }

    private void cargarDisponibilidad(Actor actor, Map<String, Map<String, Checkbox>> disponibilidadMap) {
        List<Disponibilidad> disponibilidades = disponibilidadService.obtenerPorActorId(actor.getId());
        for (Disponibilidad disponibilidad : disponibilidades) {
            if (disponibilidad.getFechaInicioSemana().equals(fechaSeleccionada)) {
                for (String dia : disponibilidadMap.keySet()) {
                    Map<String, Checkbox> dayMap = disponibilidadMap.get(dia);
                    if (dayMap != null) {
                        String[] horasDisponibles = disponibilidad.getHorasDisponibles(dia);
                        for (String hora : horasDisponibles) {
                            Checkbox checkbox = dayMap.get(hora);
                            if (checkbox != null) {
                                checkbox.setValue(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void guardarDisponibilidad(Actor actor, Map<String, Map<String, Checkbox>> disponibilidadMap) {
        Disponibilidad disponibilidad = new Disponibilidad(fechaSeleccionada);
        disponibilidad.setActorId(actor.getId());

        for (String dia : disponibilidadMap.keySet()) {
            Map<String, Checkbox> dayMap = disponibilidadMap.get(dia);
            List<String> horasDisponibles = new ArrayList<>();
            if (dayMap != null) {
                for (String hora : dayMap.keySet()) {
                    Checkbox checkbox = dayMap.get(hora);
                    if (checkbox != null && checkbox.getValue()) {
                        horasDisponibles.add(hora);
                    }
                }
                if (!horasDisponibles.isEmpty()) {
                    Collections.sort(horasDisponibles); // Ordenar las horas
                    disponibilidad.setDisponible(dia, true);
                    disponibilidad.setHorasDisponibles(dia, horasDisponibles.toArray(new String[0]));
                }
            }
        }
        disponibilidadService.guardarDisponibilidad(disponibilidad);
    }
}
