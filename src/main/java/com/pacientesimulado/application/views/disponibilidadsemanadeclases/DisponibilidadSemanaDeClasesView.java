package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.data.DisponibilidadActor;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.DisponibilidadActorService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Disponibilidad Semana de Clases")
@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
public class DisponibilidadSemanaDeClasesView extends VerticalLayout {

    private final DisponibilidadActorService disponibilidadActorService;
    private final ActorService actorService;
    private DisponibilidadActor disponibilidadActor;
    private EmailField emailField;
    private DatePicker datePicker;
    private VerticalLayout datesLayout;
    private Button saveButton;

    @Autowired
    public DisponibilidadSemanaDeClasesView(DisponibilidadActorService disponibilidadActorService, ActorService actorService) {
        this.disponibilidadActorService = disponibilidadActorService;
        this.actorService = actorService;

        add(new H2("Disponibilidad Semana de Clases"));

        emailField = new EmailField("Correo del actor");
        emailField.setPlaceholder("Ingrese el correo del actor");
        emailField.setClearButtonVisible(true);

        Button searchButton = new Button("Ingresar");
        searchButton.addClickListener(e -> verificarCorreo());

        HorizontalLayout emailLayout = new HorizontalLayout(emailField, searchButton);
        emailLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        add(emailLayout);

        datePicker = new DatePicker("Seleccione fechas");
        datePicker.setPlaceholder("Seleccione fechas");
        datePicker.setClearButtonVisible(true);
        datePicker.setEnabled(false);

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
        saveButton.setEnabled(false);

        add(datePicker, datesLayout, saveButton);

    }

    private void verificarCorreo() {
        String email = emailField.getValue();
        actorService.obtenerActorPorCorreo(email).ifPresentOrElse(
                actor -> {
                    this.disponibilidadActor = new DisponibilidadActor(actor.getId());
                    datePicker.setEnabled(true);
                    saveButton.setEnabled(true);
                    mostrarDialogoBienvenida(actor.getNombre());
                },
                () -> Notification.show("No se encontró un actor con ese correo")
        );
    }

    private void mostrarDialogoBienvenida(String nombreActor) {
        Dialog dialog = new Dialog();
        dialog.add(new Paragraph("Bienvenido, " + nombreActor));
        dialog.add(new Paragraph("Seleccione las fechas y horas de disponibilidad."));
        Button closeButton = new Button("Cerrar", e -> dialog.close());
        dialog.add(closeButton);
        dialog.open();
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
