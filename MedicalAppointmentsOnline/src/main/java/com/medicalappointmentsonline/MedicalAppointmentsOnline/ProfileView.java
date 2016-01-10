package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.medicalappointmentsonline.Services.*;

@SuppressWarnings({ "serial", "unused" })
public class ProfileView extends CustomComponent implements View {

    public static final String NAME = "profile";

    public ProfileView() {	
    	setHeight("100%");
    	HorizontalLayout horizontalLayout = new HorizontalLayout();
    	horizontalLayout.addStyleName("mainview");
    	horizontalLayout.setSizeFull();
    	
    	horizontalLayout.addComponent(new MenuView());
    	
    	if(VaadinSession.getCurrent().getAttribute(("user"))!=null ){
	    	CustomComponent content = new EditProfileLayout();
	        content.addStyleName("view-content");
	        content.setSizeFull();
	        horizontalLayout.addComponent(content);
	        horizontalLayout.setExpandRatio(content, 1.0f);
    	}
    	
        setCompositionRoot(horizontalLayout);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}