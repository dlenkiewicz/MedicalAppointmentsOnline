package com.medicalappointmentsonline.MedicalAppointmentsOnline;
 
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

import com.medicalappointmentsonline.Services.*;
 
@SuppressWarnings("serial")
public class AppointmentsMainView extends CustomComponent implements View {
 
    public static final String NAME = "";
   
    public AppointmentsMainView() {
        setHeight("100%");
        
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addStyleName("mainview");
        horizontalLayout.setSizeFull();     
        
        Component content = new AppointmentLayout();
        
        horizontalLayout.addComponent(new MenuView());
        horizontalLayout.addComponent(content);
        horizontalLayout.setExpandRatio(content, 1.0f);
        
        setCompositionRoot(horizontalLayout);
    }
    @Override
    public void enter(ViewChangeEvent event) {
    }
}