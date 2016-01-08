package com.medicalappointmentsonline.Services;

import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ConfirmationWindow extends Window {
	private EntityManagerFactory emfactory;
	private EntityManager entitymanager;
	
	private User user;
	
	private Staff doctor;
	
	private Specialization spec;
	
	private SimpleDateFormat sdfdate;
	private SimpleDateFormat sdftime;
		
	@SuppressWarnings("deprecation")
	public ConfirmationWindow(Appointment appointment) {
		Responsive.makeResponsive(this);
		setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
		addStyleName("profile-window");
		setResizable(false);
        setHeight(40.0f, Unit.PERCENTAGE);
        
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);
        
        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);
        
        detailsWrapper.addComponent(buildRegisterForm(appointment));
        content.addComponent(buildFooter(appointment));
	}

	private Component buildRegisterForm(Appointment appointment) {
		VerticalLayout root = new VerticalLayout();
		root.setCaption("Potwierdź wizytę");
        root.setIcon(FontAwesome.USER);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
		root.addStyleName("profile-form");
		
		emfactory = Persistence.createEntityManagerFactory("mao");
	 	entitymanager = emfactory.createEntityManager();
	 	
	 	Query query1 = entitymanager.createQuery("SELECT u FROM User u WHERE u.email = :value");
	 	query1.setParameter("value", VaadinSession.getCurrent().getAttribute(("user")));
	 	user = (User) query1.getSingleResult();
	 	
	 	Query query2 = entitymanager.createQuery("SELECT s FROM Staff s WHERE s.id = :value");
	 	query2.setParameter("value", appointment.getIds());
	 	doctor = (Staff) query2.getSingleResult();
	 	
	 	Query query3 = entitymanager.createQuery("SELECT spc FROM Specialization spc, Staff s, Appointment a WHERE s.id = :value and s.specid = spc.id");
	 	query3.setParameter("value", appointment.getIds());
	 	spec = (Specialization) query3.getSingleResult();
	 	
	 	appointment.setIdu(user.getId());
	 	
	 	sdfdate = new SimpleDateFormat("dd.MM.yyyy");
	 	sdftime = new SimpleDateFormat("k:mm");
	 	
		Label label = new Label("Zamierzasz zarezerwować wizytę na nazwisko " + user.getName() + " " + 
		user.getSurname() + ", która odbędzie się " + sdfdate.format(appointment.getDate()) + " roku, a " + 
		spec.getSpec() + " dr " + doctor.getName() + " " + doctor.getSurname() + 
		" będzie Cię oczekiwać o godzinie " + sdftime.format(appointment.getDate()) + 
		" w swoim gabinecie. Jeżeli wszystkie dane są… poprawne, potwierdź rezerwację terminu wizyty" +
		" klikając przycisk 'Potwierdź");
		
		root.addComponent(label);
		return root;
	}

	private Component buildFooter(Appointment appointment) {
		HorizontalLayout footer = new HorizontalLayout();
		
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		
		Button confirmButton = new Button("Potwierdź", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				entitymanager.getTransaction().begin();
				entitymanager.persist(appointment);
				entitymanager.getTransaction().commit();
				entitymanager.close();
				emfactory.close();
				
				getUI().getNavigator().navigateTo(AppointmentsMainView.NAME);
				
				Notification success = new Notification(
                        "Wizyta zarejestrowana pomyślnie");
                success.setDelayMsec(2000);
                success.setStyleName("bar success small");
                success.setPosition(Position.TOP_CENTER);
                success.show(Page.getCurrent());
				close();
			}
		});
		confirmButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		footer.addComponent(confirmButton);
		return footer;
	}

	public static void open(Appointment appointment) {
        Window window = new ConfirmationWindow(appointment);
        UI.getCurrent().addWindow(window);
        window.focus();
    }
}
