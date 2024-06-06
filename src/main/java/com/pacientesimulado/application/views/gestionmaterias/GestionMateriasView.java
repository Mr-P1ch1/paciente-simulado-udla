package com.pacientesimulado.application.views.gestionmaterias;

import com.pacientesimulado.application.data.Materia;
import com.pacientesimulado.application.services.MateriaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.html.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Route(value = "gestion-materias", layout = MainLayout.class)
@PageTitle("Gestión de Materias")

public class GestionMateriasView extends VerticalLayout {

    private final MateriaService materiaService;
    private final Grid<Materia> grid = new Grid<>(Materia.class);
    private final Binder<Materia> binder = new Binder<>(Materia.class);

    @Autowired
    public GestionMateriasView(MateriaService materiaService) {
        this.materiaService = materiaService;

        addClassName("gestion-materias-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("materia-grid");
        grid.setSizeFull();
        grid.setColumns("carrera");
        grid.addColumn(materia -> {
            StringBuilder sb = new StringBuilder();
            materia.getTiposYCasos().forEach((tipo, casos) -> {
                sb.append(tipo).append(": ").append(String.join(", ", casos)).append("\n");
            });
            return sb.toString();
        }).setHeader("Tipos y Casos");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> showFormDialog(event.getValue()));
    }

    private void configureForm() {
        // Formulario para agregar/editar materia
    }

    private void updateList() {
        grid.setItems(materiaService.obtenerTodasLasMaterias());
    }

    private void showFormDialog(Materia selectedMateria) {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        TextField carrera = new TextField("Carrera");
        TextArea casos = new TextArea("Casos");
        Paragraph explanation = new Paragraph("Para agregar los tipos y casos, use el formato:\nTipo1: Caso1, Caso2\nTipo2: Caso3, Caso4");

        formLayout.add(carrera, explanation, casos);
        dialog.add(formLayout);

        binder.forField(carrera).bind(Materia::getCarrera, Materia::setCarrera);
        binder.forField(casos).bind(
                m -> m.getTiposYCasos().entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
                        .collect( Collectors.joining("\n")),
                (m, v) -> {} // Placeholder
        );

        binder.readBean(selectedMateria);

        Button saveButton = new Button("Guardar", e -> {
            if (binder.writeBeanIfValid(selectedMateria)) {
                materiaService.guardarMateria(selectedMateria);
                updateList();
                dialog.close();
            }
        });

        Button cancelButton = new Button ("Cancelar", e -> dialog.close());

        dialog.add(saveButton, cancelButton);
        dialog.open();
    }
}
