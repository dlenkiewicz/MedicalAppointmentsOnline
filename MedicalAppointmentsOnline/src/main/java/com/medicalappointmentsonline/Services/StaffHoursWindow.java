package com.medicalappointmentsonline.Services;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import com.medicalappointmentsonline.Domain.Hours;
import com.medicalappointmentsonline.Domain.Staff;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public class StaffHoursWindow extends Window {
	private EntityManagerFactory emfactory;
	private EntityManager entitymanager;
	
	private Staff staff;
	
	private StaffHoursWindow(int staffId){
		emfactory = Persistence.createEntityManagerFactory( "mao" );
	 	entitymanager = emfactory.createEntityManager();
	 	
        loadStaff(staffId);
	 	
	 	Responsive.makeResponsive(this);
		setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
		setResizable(false);
        setClosable(false);
        setHeight(staff.getHours().size()*40 + 150, Unit.PIXELS);
        setWidth(550, Unit.PIXELS);
        
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);
        
        content.addComponent(buildHeader());
        
        Component hoursSheet = buildHoursSheet();
        hoursSheet.setSizeFull();
        content.addComponent(hoursSheet);
        content.setExpandRatio(hoursSheet, 1f);
     
        
        content.addComponent(buildFooter());
	}
	
	private Component buildHoursSheet(){
		HorizontalLayout root = new HorizontalLayout();
		
		VerticalLayout days = new VerticalLayout();
		VerticalLayout hours = new VerticalLayout();
		VerticalLayout appType = new VerticalLayout();
				
		root.addComponents(days, hours, appType);
		
		root.setExpandRatio(days, 1);
		root.setExpandRatio(hours, 1.3f);
		root.setExpandRatio(appType, 2);

		List<Hours> hoursList = staff.getHours();
		Collections.sort(hoursList, new Comparator<Hours>(){
			@Override
            public int compare(Hours lhs, Hours rhs) {
                if (lhs.getDay() > rhs.getDay())
                	return 1;
                else if(lhs.getDay() == rhs.getDay()) {
                	if(lhs.getHstart().after(rhs.getHstart()))
                		return 1;
                	else
                		return -1;
                } else
                	return -1;
            }
		});
			
		SimpleDateFormat sdftime = new SimpleDateFormat("k:mm");
		for(Hours hour : hoursList) {
			Label dayLabel = new Label();
			Label hourLabel = new Label(sdftime.format(hour.getHstart()) + " - " + sdftime.format(hour.getHend()));
			Label appTypeLabel = new Label(hour.getAppointmentType().getType());
			switch(hour.getDay()){
			case 1:
				dayLabel.setValue("Niedziela");
				break;
			case 2:
				dayLabel.setValue("Poniedziałek");
				break;
			case 3:
				dayLabel.setValue("Wtorek");
				break;
			case 4:
				dayLabel.setValue("Środa");
				break;
			case 5:
				dayLabel.setValue("Czwartek");
				break;
			case 6:
				dayLabel.setValue("Piątek");
				break;
			case 7:
				dayLabel.setValue("Sobota");
				break;
			}
			days.addComponent(dayLabel);
			hours.addComponent(hourLabel);
			appType.addComponent(appTypeLabel);
		}
		return root;
	}
	
	private void loadStaff(int staffId){
		Query query = entitymanager.createQuery( "SELECT s FROM Staff s where s.id = :value1" );
	 	query.setParameter("value1", staffId);
	 	staff = (Staff) query.getSingleResult();
	}
	
	private Component buildHeader(){
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("valo-badge");
		
		header.addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
		header.setWidth(100.0f, Unit.PERCENTAGE);
		
		Label staffName = new Label(staff.getName()+" "+staff.getSurname()+" przyjmuje w dniach:");
		staffName.setWidth(null);
		header.addComponent(staffName);
		header.setComponentAlignment(staffName, Alignment.TOP_CENTER);
		
		return header;
	}
	
	private Component buildFooter(){
		HorizontalLayout footer = new HorizontalLayout();
		
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
			
		Button cancelButton = new Button("Zamknij", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {		
				entitymanager.close();
				emfactory.close();
				close();
			}
		});
		
		footer.addComponent(cancelButton);
		footer.setComponentAlignment(cancelButton, Alignment.TOP_CENTER);	
		return footer;
	}
	
	public static void open(int staffId) {
        Window w = new StaffHoursWindow(staffId);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
