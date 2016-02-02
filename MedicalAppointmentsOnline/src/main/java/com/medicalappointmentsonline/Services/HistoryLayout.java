package com.medicalappointmentsonline.Services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.medicalappointmentsonline.Services.*;

@SuppressWarnings({ "unused", "serial" })
public class HistoryLayout extends CustomComponent {
	
	private Table historyTable;
	private EntityManagerFactory emfactory;
	private EntityManager entitymanager;
	private User user;
	
	public HistoryLayout(){
		emfactory = Persistence.createEntityManagerFactory( "mao" );
	 	entitymanager = emfactory.createEntityManager();
		
		HorizontalLayout content = new HorizontalLayout();
        content.setMargin(true);
        
        historyTable = new Table("Twoje wizyty");
		Query query = entitymanager.createQuery( "SELECT u FROM User u where u.email = :value1" );
	 	query.setParameter("value1", String.valueOf(VaadinSession.getCurrent().getAttribute(("user"))));
		try {
			user = (User) query.getSingleResult();
			historyTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class, user.getAppointments()));
		} catch (NoResultException e) {
			historyTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));
		}
		setHoursTableColumns();
		content.addComponent(historyTable);
		
        setCompositionRoot(content);
	}
	
	 private void setHoursTableColumns(){
		 
		 historyTable.addGeneratedColumn("", new ColumnGenerator() { 			    
				@Override
			    public Object generateCell(final Table source, final Object itemId, Object columnId) {
			        Button btn = new Button("Odwołaj wizytę", new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							ConfirmDialog.show(UI.getCurrent(), "Proszę potwierdź:", "Czy na pewno chcesz odwołać wizytę?",
							        "Tak", "Nie", new ConfirmDialog.Listener() {

							            public void onClose(ConfirmDialog dialog) {
							                if (dialog.isConfirmed()) {
							                	Property prop = source.getItem(itemId).getItemProperty("id");							
												entitymanager.getTransaction().begin();							
												Appointment appointment = entitymanager.find(Appointment.class, prop.getValue());							
												user.removeAppointment(appointment);							
												entitymanager.getTransaction().commit();											
												source.removeItem(itemId);
							                } else {}
							            }
							        });
						}
					});
			        btn.addStyleName(ValoTheme.BUTTON_TINY);
			        Property dateProp = source.getItem(itemId).getItemProperty("date");
			        Property hourProp = source.getItem(itemId).getItemProperty("hour");
			        
			        Date todaysDate = getTodaysDate();
			        if(todaysDate.after((Date) dateProp.getValue()) || (!todaysDate.before((Date) dateProp.getValue()) && 
			        		System.currentTimeMillis() - todaysDate.getTime() - 3600000 > ((Date) hourProp.getValue()).getTime())){
			        	return null;
			        }
			        else{
			        	return btn;
			        }
			    }
			});
		 
		 historyTable.addGeneratedColumn("hour", new ColumnGenerator(){			 
			public Object generateCell(Table source, Object itemId, Object columnId) {
			        Property prop = source.getItem(itemId).getItemProperty(columnId);
			        SimpleDateFormat sdftime = new SimpleDateFormat("k:mm");
			        Label label = new Label(sdftime.format( (Date) prop.getValue() ));
			        return label;			        
			    }
			});
		 	 
		 historyTable.addGeneratedColumn("date", new ColumnGenerator(){
			public Object generateCell(Table source, Object itemId, Object columnId) {      
			        Property prop = source.getItem(itemId).getItemProperty(columnId);       
			        SimpleDateFormat sdfday = new SimpleDateFormat("dd.MM.yyyy");
					Label label = new Label(sdfday.format( (Date) prop.getValue() ));
					return label;
			 	}
		 	});
		 
		 historyTable.addGeneratedColumn("appointmentType", new ColumnGenerator(){			 
			 public Object generateCell(Table source, Object itemId, Object columnId) {
				 	Property prop = source.getItem(itemId).getItemProperty(columnId); 			       
			        return ((AppointmentType) prop.getValue()).getType();
			    }
			});

		 historyTable.setVisibleColumns("date", "hour", "staff", "appointmentType", "");
		 historyTable.setColumnHeader("date", "Dzień");
		 historyTable.setColumnHeader("hour", "Godzina");
		 historyTable.setColumnHeader("staff", "Lekarz");
		 historyTable.setColumnHeader("appointmentType", "Rodzaj wizyty");
		 
		 historyTable.sort(new Object[]{"date","hour"}, new boolean[]{true,true});
		 
		 historyTable.setWidth("50%");
	 }
	 
	 public Date getTodaysDate(){
	    	Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        return cal.getTime();
	    }
}
