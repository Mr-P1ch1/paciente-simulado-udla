package com.pacientesimulado.application.views.gestionusuarios;

import com.pacientesimulado.application.data.Actor;
import com.pacientesimulado.application.data.Usuario;
import com.pacientesimulado.application.services.ActorService;
import com.pacientesimulado.application.services.UsuarioService;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Gestión de Usuarios")
@Route(value = "gestion-usuarios", layout = MainLayout.class)
public class GestionUsuariosView extends VerticalLayout {

    private final UsuarioService usuarioService;
    private final ActorService actorService;
    private Grid<Usuario> grid;
    private Binder<Usuario> binder;
    private Usuario usuarioSeleccionado;

    @Autowired
    public GestionUsuariosView(UsuarioService usuarioService, ActorService actorService) {
        this.usuarioService = usuarioService;
        this.actorService = actorService;
        this.grid = new Grid<>(Usuario.class);
        this.binder = new Binder<>(Usuario.class);

        configureGrid();
        configureForm();

        Button addButton = new Button("Añadir Usuario", e -> showFormDialog(new Usuario()));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button deleteSelectedButton = new Button("Eliminar Seleccionados");
        deleteSelectedButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteSelectedButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(usuario -> {
                usuarioService.eliminarUsuario(usuario.getId());
            });
            listUsuarios();
            Notification.show("Usuarios eliminados exitosamente");
        });

        add(grid, new HorizontalLayout(addButton, deleteSelectedButton));
        listUsuarios();
    }

    private void configureGrid() {
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.setColumns("nombre", "apellido", "correo", "rol");

        grid.addColumn(new ComponentRenderer<>(usuario -> {
            Button editButton = new Button("Editar");
            editButton.addClickListener(event -> showFormDialog(usuario));
            return editButton;
        })).setHeader("Editar");

        grid.addColumn(new ComponentRenderer<>(usuario -> {
            Button deleteButton = new Button("Eliminar");
            deleteButton.addClickListener(event -> {
                usuarioService.eliminarUsuario(usuario.getId());
                listUsuarios();
            });
            return deleteButton;
        })).setHeader("Eliminar");
    }

    private void configureForm() {
        // No es necesario volver a asignar el binder aquí
    }

    private void showFormDialog(Usuario usuario) {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        TextField nombre = new TextField("Nombre");
        TextField apellido = new TextField("Apellido");
        EmailField correo = new EmailField("Correo");
        correo.setWidthFull();
        correo.setErrorMessage("Por favor, ingrese un correo válido.");
        correo.setPattern("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

        PasswordField contraseña = new PasswordField("Contraseña");
        ComboBox<String> rol = new ComboBox<>("Rol");
        rol.setItems("Actor", "Doctor", "Administrador");

        // Campos adicionales para actores
        NumberField edad = new NumberField("Edad");
        ComboBox<String> sexo = new ComboBox<>("Sexo");
        sexo.setItems("Femenino", "Masculino");
        NumberField peso = new NumberField("Peso");
        NumberField altura = new NumberField("Altura");

        binder.forField(nombre).asRequired("Nombre es requerido").bind(Usuario::getNombre, Usuario::setNombre);
        binder.forField(apellido).asRequired("Apellido es requerido").bind(Usuario::getApellido, Usuario::setApellido);
        binder.forField(correo)
                .withValidator(new EmailValidator("Correo inválido"))
                .asRequired("Correo es requerido")
                .withValidationStatusHandler(status -> {
                    correo.setInvalid(status.isError());
                    correo.setErrorMessage(status.getMessage().orElse(""));
                })
                .bind(Usuario::getCorreo, Usuario::setCorreo);
        binder.forField(contraseña).asRequired("Contraseña es requerida").bind(Usuario::getContraseña, Usuario::setContraseña);
        binder.forField(rol).asRequired("Rol es requerido").bind(Usuario::getRol, Usuario::setRol);

        // Aplicar validaciones y bindings a los campos adicionales
        binder.forField(edad)
                .withConverter(new DoubleToIntegerConverter())
                .withValidator(p -> p == null || (p >= 5 && p <= 90), "La edad debe estar entre 5 y 90 años")
                .bind(actor -> actorService.obtenerActorPorCorreo(actor.getCorreo()).map(Actor::getEdad).orElse(null),
                        (actor, value) -> actorService.obtenerActorPorCorreo(actor.getCorreo()).ifPresent(a -> a.setEdad(value)));

        binder.forField(sexo)
                .bind(actor -> actorService.obtenerActorPorCorreo(actor.getCorreo()).map(Actor::getSexo).orElse(null),
                        (actor, value) -> actorService.obtenerActorPorCorreo(actor.getCorreo()).ifPresent(a -> a.setSexo(value)));

        binder.forField(peso)
                .withValidator(p -> p == null || (p >= 30 && p <= 300), "Peso debe estar entre 30 kg y 300 kg")
                .bind(actor -> actorService.obtenerActorPorCorreo(actor.getCorreo()).map(Actor::getPeso).orElse(null),
                        (actor, value) -> actorService.obtenerActorPorCorreo(actor.getCorreo()).ifPresent(a -> a.setPeso(value)));

        binder.forField(altura)
                .withValidator(a -> a == null || (a >= 1.0 && a <= 2.50), "Altura debe estar entre 1.0 m y 2.50 m")
                .bind(actor -> actorService.obtenerActorPorCorreo(actor.getCorreo()).map(Actor::getAltura).orElse(null),
                        (actor, value) -> actorService.obtenerActorPorCorreo(actor.getCorreo()).ifPresent(a -> a.setAltura(value)));

        binder.readBean(usuario);

        formLayout.add(nombre, apellido, correo, contraseña, rol);

        rol.addValueChangeListener(event -> {
            if ("Actor".equals(event.getValue())) {
                formLayout.add(edad, sexo, peso, altura);
            } else {
                formLayout.remove(edad, sexo, peso, altura);
            }
        });

        if ("Actor".equals(usuario.getRol())) {
            formLayout.add(edad, sexo, peso, altura);
            actorService.obtenerActorPorCorreo(usuario.getCorreo()).ifPresent(actor -> {
                edad.setValue(actor.getEdad() != 0 ? Double.valueOf(actor.getEdad()) : null);
                sexo.setValue(actor.getSexo());
                peso.setValue(actor.getPeso());
                altura.setValue(actor.getAltura());
            });
        }

        Button saveButton = new Button("Guardar", event -> {
            try {
                // Limpiar mensajes de error antes de intentar guardar
                binder.validate();

                if (!binder.isValid()) {
                    return;
                }

                binder.writeBean(usuario);

                if (usuario.getId() == null) {
                    usuarioService.registrarUsuario(usuario);
                    if ("Actor".equals(usuario.getRol())) {
                        Actor actor = new Actor();
                        actor.setNombre(usuario.getNombre());
                        actor.setCorreo(usuario.getCorreo());
                        actor.setEdad(edad.getValue() != null ? edad.getValue().intValue() : 0);
                        actor.setSexo(sexo.getValue());
                        actor.setPeso(peso.getValue());
                        actor.setAltura(altura.getValue());
                        actorService.guardarActor(actor);
                    }
                    Notification.show("Usuario creado");
                } else {
                    usuarioService.actualizarUsuario(usuario.getId(), usuario);
                    if ("Actor".equals(usuario.getRol())) {
                        Actor actor = actorService.obtenerActorPorCorreo(usuario.getCorreo()).orElse(new Actor());
                        actor.setNombre(usuario.getNombre());
                        actor.setCorreo(usuario.getCorreo());
                        actor.setEdad(edad.getValue() != null ? edad.getValue().intValue() : 0);
                        actor.setSexo(sexo.getValue());
                        actor.setPeso(peso.getValue());
                        actor.setAltura(altura.getValue());
                        actorService.guardarActor(actor);
                    }
                    Notification.show("Usuario actualizado");
                }
                dialog.close();
                listUsuarios();
            } catch (ValidationException e) {
                Notification.show("Error al guardar usuario: " + e.getValidationErrors().stream()
                        .map(error -> error.getErrorMessage()).reduce((s1, s2) -> s1 + ", " + s2).orElse(""));
            } catch (Exception e) {
                Notification.show("Error al guardar usuario: " + e.getMessage());
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        dialog.add(formLayout, buttonsLayout);

        dialog.open();
    }

    private void listUsuarios() {
        grid.setItems(usuarioService.obtenerTodos());
    }

    private static class DoubleToIntegerConverter implements Converter<Double, Integer> {
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
