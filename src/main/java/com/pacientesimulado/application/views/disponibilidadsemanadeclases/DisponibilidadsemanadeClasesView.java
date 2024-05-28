/*package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Disponibilidad semana de Clases")
@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
@RolesAllowed("USER")
//Modificar para que no sea desde admin

public class DisponibilidadsemanadeClasesView extends Composite<VerticalLayout> {

    public DisponibilidadsemanadeClasesView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        DatePicker datePicker = new DatePicker();
        H6 h6 = new H6();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H6 h62 = new H6();
        H6 h63 = new H6();
        H6 h64 = new H6();
        H6 h65 = new H6();
        H6 h66 = new H6();
        H6 h67 = new H6();
        H6 h68 = new H6();
        H6 h69 = new H6();
        H6 h610 = new H6();
        H6 h611 = new H6();
        H6 h612 = new H6();
        H6 h613 = new H6();
        H6 h614 = new H6();
        H6 h615 = new H6();
        H6 h616 = new H6();
        H6 h617 = new H6();
        H6 h618 = new H6();
        H6 h619 = new H6();
        H6 h620 = new H6();
        H6 h621 = new H6();
        Button buttonPrimary = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("600px");
        layoutRow.setHeight("min-content");
        datePicker.setLabel("Semana de clases disponible");
        datePicker.setWidth("226px");
        h6.setText("Clases Módulos");
        h6.setWidth("max-content");
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        layoutColumn2.setHeightFull();
        layoutRow2.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("69px");
        layoutColumn2.getStyle().set("flex-grow", "1");
        h62.setText("7h00");
        h62.setWidth("max-content");
        h63.setText("8h05");
        h63.setWidth("max-content");
        h64.setText("9h10");
        h64.setWidth("max-content");
        h65.setText("10h15");
        h65.setWidth("max-content");
        h66.setText("11h20");
        h66.setWidth("max-content");
        h67.setText("12h25");
        h67.setWidth("max-content");
        h68.setText("13h30");
        h68.setWidth("max-content");
        h69.setText("14h35");
        h69.setWidth("max-content");
        h610.setText("15h40");
        h610.setWidth("max-content");
        h611.setText("16h45");
        h611.setWidth("max-content");
        h612.setText("17h45");
        h612.setWidth("max-content");
        h613.setText("18h50");
        h613.setWidth("max-content");
        h614.setText("19h50");
        h614.setWidth("max-content");
        h615.setText("Lunes");
        h615.setWidth("max-content");
        h616.setText("Martes");
        h616.setWidth("max-content");
        h617.setText("Miércoles");
        h617.setWidth("max-content");
        h618.setText("Jueves");
        h618.setWidth("max-content");
        h619.setText("Viernes");
        h619.setWidth("max-content");
        h620.setText("Sabado");
        h620.setWidth("max-content");
        h621.setText("Domingo");
        h621.setWidth("max-content");
        buttonPrimary.setText("Enviar");
        getContent().setAlignSelf(FlexComponent.Alignment.END, buttonPrimary);
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(layoutRow);
        layoutRow.add(datePicker);
        getContent().add(h6);
        getContent().add(layoutRow2);
        layoutRow2.add(layoutColumn2);
        layoutColumn2.add(h62);
        layoutColumn2.add(h63);
        layoutColumn2.add(h64);
        layoutColumn2.add(h65);
        layoutColumn2.add(h66);
        layoutColumn2.add(h67);
        layoutColumn2.add(h68);
        layoutColumn2.add(h69);
        layoutColumn2.add(h610);
        layoutColumn2.add(h611);
        layoutColumn2.add(h612);
        layoutColumn2.add(h613);
        layoutColumn2.add(h614);
        layoutRow2.add(h615);
        layoutRow2.add(h616);
        layoutRow2.add(h617);
        layoutRow2.add(h618);
        layoutRow2.add(h619);
        layoutRow2.add(h620);
        layoutRow2.add(h621);
        getContent().add(buttonPrimary);

    }
}*/
package com.pacientesimulado.application.views.disponibilidadsemanadeclases;

import com.pacientesimulado.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Disponibilidad semana de Clases")
@Route(value = "disponibilidad-semana-de-clases", layout = MainLayout.class)
@RolesAllowed("USER")
public class DisponibilidadsemanadeClasesView extends Composite<VerticalLayout> {

    public DisponibilidadsemanadeClasesView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        DatePicker datePicker = new DatePicker();
        H6 h6 = new H6("Clases Módulos");

        // Contenedor principal para la cuadrícula de disponibilidad
        VerticalLayout mainGrid = new VerticalLayout();
        mainGrid.setWidthFull();
        mainGrid.setSpacing(true);

        // Crear un GridLayout para alinear los días y las horas correctamente
        Grid<Disponibilidad> grid = new Grid<>(Disponibilidad.class, false);
        grid.addColumn(Disponibilidad::getHora).setHeader("Hora").setAutoWidth(true);

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (String dia : dias) {
            grid.addComponentColumn(disponibilidad -> {
                Checkbox checkbox = new Checkbox();
                checkbox.setValue(disponibilidad.isDisponible(dia));
                return checkbox;
            }).setHeader(dia).setAutoWidth(true);
        }

        grid.setItems(
                new Disponibilidad("7h00"), new Disponibilidad("8h05"), new Disponibilidad("9h10"),
                new Disponibilidad("10h15"), new Disponibilidad("11h20"), new Disponibilidad("12h25"),
                new Disponibilidad("13h30"), new Disponibilidad("14h35"), new Disponibilidad("15h40"),
                new Disponibilidad("16h45"), new Disponibilidad("17h45"), new Disponibilidad("18h50"),
                new Disponibilidad("19h50")
        );
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        Button buttonPrimary = new Button("Enviar");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Configuración del layout principal
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().addClassName(Gap.MEDIUM);
        getContent().add(datePicker, h6, grid, buttonPrimary);
        getContent().setAlignSelf(FlexComponent.Alignment.END, buttonPrimary);
    }

    private static class Disponibilidad {
        private final String hora;
        private final boolean[] disponibilidad;

        public Disponibilidad(String hora) {
            this.hora = hora;
            this.disponibilidad = new boolean[7]; // Inicializar con falso
        }

        public String getHora() {
            return hora;
        }

        public boolean isDisponible(String dia) {
            int index = switch (dia) {
                case "Lunes" -> 0;
                case "Martes" -> 1;
                case "Miércoles" -> 2;
                case "Jueves" -> 3;
                case "Viernes" -> 4;
                case "Sábado" -> 5;
                case "Domingo" -> 6;
                default -> throw new IllegalArgumentException("Día inválido: " + dia);
            };
            return disponibilidad[index];
        }

        public void setDisponible(String dia, boolean disponible) {
            int index = switch (dia) {
                case "Lunes" -> 0;
                case "Martes" -> 1;
                case "Miércoles" -> 2;
                case "Jueves" -> 3;
                case "Viernes" -> 4;
                case "Sábado" -> 5;
                case "Domingo" -> 6;
                default -> throw new IllegalArgumentException("Día inválido: " + dia);
            };
            disponibilidad[index] = disponible;
        }
    }
}

