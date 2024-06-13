package com.pacientesimulado.application.views.disponibilidadpracticadoctor;

import com.pacientesimulado.application.data.Disponibilidad;
import com.pacientesimulado.application.data.Doctor;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.DoctorService;
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

@Route(value = "disponibilidad-practica-doctor", layout = MainLayout.class)
@PageTitle("Disponibilidad Práctica Doctor")
public class DisponibilidadPracticaDoctorView extends VerticalLayout {

    private final DoctorService doctorService;
    private DatePicker datePicker;
    private VerticalLayout datesLayout;
    private Button saveButton;
    private Paragraph welcomeMessage = new Paragraph();

    @Autowired
    public DisponibilidadPracticaDoctorView(DoctorService doctorService) {
        this.doctorService = doctorService;

        Usuario currentUser = VaadinSession.getCurrent().getAttribute(Usuario.class);

        add(new H2("Disponibilidad Práctica Doctor"));
        welcomeMessage.setText("Bienvenido, " + currentUser.getNombre() + " " + currentUser.getApellido() + ". Seleccione las fechas y horas de disponibilidad para las entranamientos con el actor.");
        add(welcomeMessage);

        datePicker = new DatePicker("Seleccione una o varias fechas");
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
        Optional<Doctor> optionalDoctor = doctorService.obtenerDoctorPorCorreo(currentUser.getCorreo());

        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            List<Disponibilidad> disponibilidades = new ArrayList<>();
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
                disponibilidades.add(disponibilidad);
            }
            doctor.setDisponibilidades(disponibilidades);
            doctorService.guardarDoctor(doctor);
            Notification.show("Disponibilidad guardada");
        } else {
            Notification.show("Doctor no encontrado");
        }
        datesLayout.removeAll();
    }
}
