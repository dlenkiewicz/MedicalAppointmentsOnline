package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.medicalappointmentsonline.Services.*;

@SuppressWarnings({ "serial", "unused" })
public class HistoryView extends CustomComponent implements View {
	
	public static final String NAME = "history";
	
	public HistoryView() {
		setHeight("100%");
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.addComponent(new MenuView());
	 	
		CustomComponent content = new HistoryLayout();
        content.addStyleName("view-content");
        content.setSizeFull();
        horizontalLayout.addComponent(content);
        horizontalLayout.setExpandRatio(content, 1.0f);
	 	
		setCompositionRoot(horizontalLayout);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
