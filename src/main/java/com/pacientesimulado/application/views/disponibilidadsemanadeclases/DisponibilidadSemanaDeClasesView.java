package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.data.DisponibilidadActor;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.DisponibilidadActorService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
@PageTitle("Disponibilidad Semana de Clases")
public class DisponibilidadSemanaDeClasesView extends VerticalLayout {

    private final DisponibilidadActorService disponibilidadActorService;
    private final ActorService actorService;
    private DisponibilidadActor disponibilidadActor;
    private DatePicker datePicker;
    private VerticalLayout datesLayout;
    private Button saveButton;
    private Paragraph welcomeMessage = new Paragraph();

    @Autowired
    public DisponibilidadSemanaDeClasesView(DisponibilidadActorService disponibilidadActorService, ActorService actorService) {
        this.disponibilidadActorService = disponibilidadActorService;
        this.actorService = actorService;

        Usuario currentUser = VaadinSession.getCurrent().getAttribute(Usuario.class);

        add(new H2("Disponibilidad Semana de Clases"));
        welcomeMessage.setText("Bienvenido, " + currentUser.getNombre() +" "+ currentUser.getApellido ()  + ". Seleccione las fechas y horas de disponibilidad.");
        add(welcomeMessage);

        datePicker = new DatePicker("Seleccione fechas");
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

        MultiSelectComboBox<String> hoursComboBox = new MultiSelectComboBox<>("Seleccione horas");
        hoursComboBox.setItems(
                "7h00", "8h05", "9h10", "10h15", "11h20",
                "12h25", "13h30", "14h35", "15h40", "16h45",
                "17h45", "18h50", "19h50"
        );
        hoursComboBox.addValueChangeListener(event -> {
            List<String> hours = new ArrayList<>(hoursComboBox.getSelectedItems());
            disponibilidadActor.addDisponibilidad(date, hours);
        });

        dateLayout.add(hoursComboBox);
        datesLayout.add(dateLayout);
    }

    private void guardarDisponibilidad() {
        disponibilidadActorService.guardarDisponibilidad(disponibilidadActor);
        Notification.show("Disponibilidad guardada");
        datesLayout.removeAll();
    }
}
