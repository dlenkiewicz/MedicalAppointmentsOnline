package com.medicalappointmentsonline.Services;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

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
	
	@SuppressWarnings("deprecation")
	public HistoryLayout(){
		emfactory = Persistence.createEntityManagerFactory( "mao" );
	 	entitymanager = emfactory.createEntityManager();
		
		HorizontalLayout content = new HorizontalLayout();
        content.setMargin(true);
        
        historyTable = new Table("Twoje wizyty");
		Query query = entitymanager.createQuery( "SELECT u FROM User u where u.email = :value1" );
	 	query.setParameter("value1", String.valueOf(VaadinSession.getCurrent().getAttribute(("user"))));
		try {
			User user = (User) query.getSingleResult();
			BeanItemContainer<Appointment> tmp = new BeanItemContainer<Appointment>(user.getAppointments());
			historyTable.setContainerDataSource(tmp);
		} catch (NoResultException e) {
			historyTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));
		} catch (IllegalArgumentException e){
			historyTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));
		}
		setHoursTableColumns();
		content.addComponent(historyTable);
		
        setCompositionRoot(content);
	}
	
	 private void setHoursTableColumns(){
		 
		 historyTable.addGeneratedColumn("Action", new ColumnGenerator() { 
			    @SuppressWarnings("rawtypes")
				@Override
			    public Object generateCell(final Table source, final Object itemId, Object columnId) {
			        Button btn = new Button("Delete", new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {		
							entitymanager.getTransaction().begin();
							Property prop = source.getItem(itemId).getItemProperty("id");
							Query query = entitymanager.createQuery("DELETE FROM Appointment a WHERE a.id = :value1");
							query.setParameter("value1", (int) prop.getValue());
							query.executeUpdate();
							entitymanager.getTransaction().commit();

						}
					});
			        btn.addStyleName(ValoTheme.BUTTON_TINY);
			        Property prop = source.getItem(itemId).getItemProperty("date");
			        
			        Date todaysDate = AppointmentLayout.getTodaysDate();
			        if(todaysDate.after((Date) prop.getValue())){
			        	return null;
			        }
			        else{
			        	return btn;
			        }
			    }
			});
		 
		 historyTable.addGeneratedColumn("hour", new ColumnGenerator(){			 
			 @SuppressWarnings("rawtypes")
			public Object generateCell(Table source, Object itemId, Object columnId) {
			        Property prop = source.getItem(itemId).getItemProperty(columnId);
			        SimpleDateFormat sdftime = new SimpleDateFormat("k:mm");
			        Label label = new Label(sdftime.format( (Date) prop.getValue() ));
			        return label;			        
			    }
			});
		 	 
		 historyTable.addGeneratedColumn("date", new ColumnGenerator(){
			 @SuppressWarnings("rawtypes")
			public Object generateCell(Table source, Object itemId, Object columnId) {      
			        Property prop = source.getItem(itemId).getItemProperty(columnId);       
			        SimpleDateFormat sdfday = new SimpleDateFormat("dd.MM.yyyy");
					Label label = new Label(sdfday.format( (Date) prop.getValue() ));
					return label;
			 	}
		 	});
		 
		 historyTable.addGeneratedColumn("id", new ColumnGenerator(){			 
			 @SuppressWarnings("rawtypes")
			public Object generateCell(Table source, Object itemId, Object columnId) {
				 	Property prop = source.getItem(itemId).getItemProperty(columnId); 
			        Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Appointment a "
			        		+ "WHERE h.staff.id = a.staff.id AND a.hour >= h.hstart AND "
			        		+ "a.hour <= h.hend AND SQL('EXTRACT(dow from ?)', a.date) + 1 = h.day AND "
			        		+ "a.id = :value1" );
			        query.setParameter("value1",(int) prop.getValue());
			        query.setMaxResults(1);
			        Hours hour = (Hours) query.getResultList().get(0);
			        return hour.getAppointmentType().getType();
			    }
			});
		 
		 historyTable.setVisibleColumns("date", "hour", "staff", "id", "Action");
		 historyTable.setColumnHeader("date", "Dzień");
		 historyTable.setColumnHeader("hour", "Godzina");
		 historyTable.setColumnHeader("staff", "Lekarz");
		 historyTable.setColumnHeader("id", "Rodzaj wizyty");
		 
		 historyTable.sort(new Object[]{"date","hour"}, new boolean[]{true,true});
		 
		 historyTable.setWidth("50%");
	 }
}
