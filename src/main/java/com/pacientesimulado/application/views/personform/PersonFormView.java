package com.pacientesimulado.application.views.personform;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Actualizar Datos del Actor")
@Route(value = "person-form", layout = MainLayout.class)
public class PersonFormView extends VerticalLayout {

    private final ActorService actorService;
    private Actor actor;

    private EmailField correoField = new EmailField("Correo del actor");
    private NumberField edadField = new NumberField("Edad");
    private NumberField pesoField = new NumberField("Peso");
    private NumberField alturaField = new NumberField("Altura");
    private Paragraph welcomeMessage = new Paragraph();

    private Binder<Actor> binder = new Binder<>(Actor.class);

    @Autowired
    public PersonFormView(ActorService actorService) {
        this.actorService = actorService;

        add(new H2("Actualizar Datos del Actor"));

        // Diseño de búsqueda
        HorizontalLayout searchLayout = createSearchLayout();
        add(searchLayout, welcomeMessage);

        FormLayout formLayout = new FormLayout();
        formLayout.add(edadField, pesoField, alturaField);

        Button saveButton = new Button("Guardar Datos", e -> guardarDatosActor());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton);

        add(formLayout, buttonsLayout);

        binder.forField(edadField)
                .withConverter(new DoubleToIntegerConverter())
                .asRequired("Edad es requerida")
                .withValidator(p -> p != null && p >= 5 && p <= 90, "La edad debe estar entre 5 y 90 años")
                .bind(Actor::getEdad, Actor::setEdad);

        binder.forField(pesoField)
                .asRequired("Peso es requerido")
                .withValidator(p -> p != null && p >= 30 && p <= 300, "Peso debe estar entre 30 kg y 300 kg")
                .bind(Actor::getPeso, Actor::setPeso);

        binder.forField(alturaField)
                .asRequired("Altura es requerida")
                .withValidator(a -> a != null && a >= 1.0 && a <= 2.50, "Altura debe estar entre 1.0 m y 2.50 m")
                .bind(Actor::getAltura, Actor::setAltura);

        binder.bindInstanceFields(this);
    }

    private HorizontalLayout createSearchLayout() {
        EmailField emailField = new EmailField("Correo del actor");
        emailField.setPlaceholder("Ingrese el correo del actor");

        Button searchButton = new Button("Buscar");
        searchButton.getElement().getStyle().set("margin-top", "auto"); // Alinea el botón con el campo de correo
        searchButton.addClickListener(e -> {
            String email = emailField.getValue();
            actorService.obtenerActorPorCorreo(email).ifPresentOrElse(
                    actor -> {
                        this.actor = actor;
                        binder.readBean(actor);
                        welcomeMessage.setText("Bienvenido, " + actor.getNombre() );
                    },
                    () -> Notification.show("No se encontró actor con ese correo")
            );
        });

        HorizontalLayout layout = new HorizontalLayout(emailField, searchButton);
        layout.setDefaultVerticalComponentAlignment(Alignment.END);
        return layout;
    }

    private void guardarDatosActor() {
        if (actor == null) {
            Notification.show("Por favor, busque un actor primero.");
            return;
        }
        if (binder.writeBeanIfValid(actor)) {
            actorService.guardarActor(actor);
            Notification.show("Datos guardados correctamente.");
        } else {
            Notification.show("Por favor, complete todos los campos correctamente.");
        }
    }

    private class DoubleToIntegerConverter implements Converter<Double, Integer> {
        @Override
        public Result<Integer> convertToModel(Double value, ValueContext context) {
            return value == null ? Result.ok(null) : Result.ok(value.intValue());
        }

        @Override
        public Double convertToPresentation(Integer value, ValueContext context) {
            return value == null ? null : value.doubleValue();
        }
    }
}
