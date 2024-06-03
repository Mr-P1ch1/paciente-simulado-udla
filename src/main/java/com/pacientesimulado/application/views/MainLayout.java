package com.pacientesimulado.application.views;
import com.pacientesimulado.application.views.disponibilidadsemanadeclases.DisponibilidadSemanaDeClasesView;
import com.pacientesimulado.application.views.gestionmaterias.GestionMateriasView;
import com.pacientesimulado.application.views.gestionusuarios.GestionUsuariosView;
import com.pacientesimulado.application.views.personform.PersonFormView;
import com.pacientesimulado.application.views.reservaprogramapacientesimuladoactor.ReservaProgramaPacienteSimuladoACTORView;
import com.pacientesimulado.application.views.solicitudesdereserva.SolicitudesdereservaView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.applayout.DrawerToggle;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Sistema de Gestión de Pacientes Simulados");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Padding.Vertical.SMALL);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new RouterLink("Actualizar Datos del Actor", PersonFormView.class));
        addToDrawer(new RouterLink("Disponibilidad Semana de Clases", DisponibilidadSemanaDeClasesView.class));
        addToDrawer(new RouterLink("Gestión de Usuarios", GestionUsuariosView.class));
        addToDrawer(new RouterLink("Reserva Programa Paciente Simulado (ACTOR)", ReservaProgramaPacienteSimuladoACTORView.class));
        addToDrawer(new RouterLink("Gestión de Materias", GestionMateriasView.class));
        addToDrawer(new RouterLink("Solicitudes de Reserva", SolicitudesdereservaView.class));
    }
}
