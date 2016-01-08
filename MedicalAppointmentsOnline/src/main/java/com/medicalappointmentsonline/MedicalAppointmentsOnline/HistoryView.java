package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class HistoryView extends CustomComponent implements View {
	public static final String NAME = "history";
	
	EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("mao");
    EntityManager entitymanager = emfactory.createEntityManager();
    
	private Table history;
	
	public HistoryView() {
		setHeight("100%");
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		
		/*Query query = entitymanager.createQuery("SELECT a FROM Staff s," +
		"Appointment a WHERE a.ids = s.id");//a.idu = :value AND 
		query.setParameter("value", getSession().getAttribute("user"));
		List<Appointment> result = query.getResultList();
		history = new Table("Twoje wizyty");
		history.addItems(result);*/
		
		horizontalLayout.addComponent(new MenuView());
		horizontalLayout.addComponent(history);
		setCompositionRoot(horizontalLayout);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
