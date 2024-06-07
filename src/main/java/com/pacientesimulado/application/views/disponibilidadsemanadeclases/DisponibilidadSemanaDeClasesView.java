package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.DisponibilidadService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
@PageTitle("Disponibilidad Semana de Clases")
public class DisponibilidadSemanaDeClasesView extends VerticalLayout {

    private final DisponibilidadService disponibilidadService;
    private final ActorService actorService;
    private DatePicker datePicker;
    private VerticalLayout datesLayout;
    private Button saveButton;
    private Paragraph welcomeMessage = new Paragraph();

    @Autowired
    public DisponibilidadSemanaDeClasesView(DisponibilidadService disponibilidadService, ActorService actorService) {
        this.disponibilidadService = disponibilidadService;
        this.actorService = actorService;

        Usuario currentUser = VaadinSession.getCurrent().getAttribute(Usuario.class);

        add(new H2("Disponibilidad Semana de Clases"));
        welcomeMessage.setText("Bienvenido, " + currentUser.getNombre() + " " + currentUser.getApellido() + ". Seleccione las fechas y horas de disponibilidad.");
        add(welcomeMessage);

        datePicker = new DatePicker("Seleccione una o varias fechas");
        datePicker.setWidth("400px");
        datePicker.setPlaceholder("Seleccione fechas");
        datePicker.setClearButtonVisible(true);
        datePicker.setEnabled(true);

        datesLayout = new VerticalLayout();
        datesLayout.setSpacing(true);
        datesLayout.setPadding(true);

        datePicker.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            if (selectedDate != null) {
                addDateWithHours(selectedDate);
                datePicker.clear();
            }
        });

        saveButton = new Button("Guardar Disponibilidad", event -> guardarDisponibilidad());
        saveButton.setEnabled(true);

        add(datePicker, datesLayout, saveButton);
    }

    private void addDateWithHours(LocalDate date) {
        VerticalLayout dateLayout = new VerticalLayout();
        String formattedDate = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es")) + " " +
                date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dateLayout.add(new H2(formattedDate));

        MultiSelectComboBox<String> hoursComboBox = new MultiSelectComboBox<>("Seleccione una o varias horas");
        hoursComboBox.setWidth("400px");
        hoursComboBox.setItems(
                "07h00", "08h05", "09h10", "10h15", "11h20",
                "12h25", "13h30", "14h35", "15h40", "16h45",
                "17h45", "18h50", "19h50"
        );

        dateLayout.add(hoursComboBox);
        datesLayout.add(dateLayout);
    }

    private void guardarDisponibilidad() {
        Usuario currentUser = VaadinSession.getCurrent().getAttribute(Usuario.class);
        actorService.obtenerActorPorCorreo(currentUser.getCorreo()).ifPresent(actor -> {
            List<Disponibilidad> nuevasDisponibilidades = new ArrayList<>();
            for (int i = 0; i < datesLayout.getComponentCount(); i++) {
                VerticalLayout dateLayout = (VerticalLayout) datesLayout.getComponentAt(i);
                String dateText = ((H2) dateLayout.getComponentAt(0)).getText();
                MultiSelectComboBox<String> hoursComboBox = (MultiSelectComboBox<String>) dateLayout.getComponentAt(1);

                LocalDate date = LocalDate.parse(dateText.split(" ")[1], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                Disponibilidad disponibilidad = new Disponibilidad();
                disponibilidad.setFecha(date);

                List<Disponibilidad.HoraDisponibilidad> horas = new ArrayList<>();
                for (String hour : hoursComboBox.getSelectedItems()) {
                    Disponibilidad.HoraDisponibilidad horaDisponibilidad = new Disponibilidad.HoraDisponibilidad();
                    horaDisponibilidad.setHora(hour);
                    horaDisponibilidad.setEstado("libre");
                    horas.add(horaDisponibilidad);
                }
                disponibilidad.setHoras(horas);
                nuevasDisponibilidades.add(disponibilidad);
            }

            // Obtener las disponibilidades existentes y combinarlas con las nuevas
            List<Disponibilidad> disponibilidadesExistentes = actor.getDisponibilidades();
            List<Disponibilidad> disponibilidadesCombinadas = new ArrayList<>(disponibilidadesExistentes);

            for (Disponibilidad nueva : nuevasDisponibilidades) {
                Optional<Disponibilidad> existente = disponibilidadesExistentes.stream()
                        .filter(d -> d.getFecha().equals(nueva.getFecha()))
                        .findFirst();

                if (existente.isPresent()) {
                    // Si la fecha ya existe, agregar las nuevas horas a las existentes
                    List<Disponibilidad.HoraDisponibilidad> horasCombinadas = new ArrayList<>(existente.get().getHoras());
                    horasCombinadas.addAll(nueva.getHoras());
                    existente.get().setHoras(horasCombinadas);
                } else {
                    // Si la fecha no existe, agregar la nueva disponibilidad
                    disponibilidadesCombinadas.add(nueva);
                }
            }

            actor.setDisponibilidades(disponibilidadesCombinadas);
            actorService.guardarActor(actor);
            Notification.show("Disponibilidad guardada");
        });
        datesLayout.removeAll();
    }
}
