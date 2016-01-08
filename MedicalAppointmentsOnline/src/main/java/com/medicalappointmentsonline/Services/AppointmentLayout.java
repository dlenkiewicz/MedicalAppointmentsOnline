package com.medicalappointmentsonline.Services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.validator.DateRangeValidator;
import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class AppointmentLayout extends CustomComponent {
	private Table staffTable;
    private Table hoursTable;
    private Table appointmentTable;
    
    private ComboBox specSelect;
    private DateField dateSelect;
    
    private JPAContainer<Specialization> specs;
    private JPAContainer<Staff> staff;
    
    private List<Hours> hours;
    private List<Appointment> appointmentList;
    
    @SuppressWarnings("deprecation")
	public AppointmentLayout(){
    	HorizontalLayout content = new HorizontalLayout();
        content.setMargin(true);
       
        VerticalLayout verticalLayout = new VerticalLayout();
        
        staff = JPAContainerFactory.make(Staff.class,
                AppointmentsUI.PERSISTENCE_UNIT);
        specs = JPAContainerFactory.make(Specialization.class,
                 AppointmentsUI.PERSISTENCE_UNIT);

        Calendar cal = Calendar.getInstance();
        
        dateSelect = new DateField("Wybierz dzień");
        dateSelect.addValidator(new DateRangeValidator("Podana data jest spoza możliwych terminów przyjęć", 
        		getTodaysDate(), getMaxDate(), Resolution.DAY));
        dateSelect.setValue(getTodaysDate());
        
        staffTable = new Table("Wybierz lekarza:", staff);
        staffTable.setSelectable(true);
        staffTable.setImmediate(true);
        staffTable.setVisibleColumns("name", "surname");
        staffTable.setColumnHeader("name", "Imię");
        staffTable.setColumnHeader("surname", "Nazwisko");
       
        specSelect = new ComboBox("Wybierz specjalistę", specs);
        specSelect.setItemCaptionPropertyId("spec");
        specSelect.setNullSelectionAllowed(false);
        specSelect.setFilteringMode(FilteringMode.CONTAINS);
        
        hoursTable = new Table("Wybierz termin wizyty:");
        hoursTable.setImmediate(true);
        hoursTable.setContainerDataSource(new BeanItemContainer<>(Hours.class));
        setHoursTableColumns();
        
        appointmentTable = new Table("Wybierz termin wizyty:");
        appointmentTable.setImmediate(true);
        appointmentTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));
        appointmentTable.setSelectable(true);
        appointmentTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        appointmentTable.setWidth("70%");
        setAppointmentsTableColumns();
        
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("mao");
        final EntityManager entitymanager = emfactory.createEntityManager();
        
        specSelect.addValueChangeListener(new Property.ValueChangeListener() {
        	Filter filter = null;
        	
        	@SuppressWarnings("unchecked")
			public void valueChange(ValueChangeEvent event) {
                Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s, Specialization spc "
                		+ "where s.id = h.id and spc.id = s.specid and spc.id = :value1 and h.day = :value2" );               
                cal.setTime(dateSelect.getValue());
                
                query.setParameter("value1", specSelect.getValue());
                query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
                hours = query.getResultList();
                             
                if (specSelect.getValue() != null) {
		        	if (filter != null)
		        		staff.removeContainerFilter(filter);
		        	filter = new Compare.Equal("specid", specSelect.getValue());
		        	staff.addContainerFilter(filter);
		        }                       
                generateAppointments();
            }
        });
        
        staffTable.addValueChangeListener(new Property.ValueChangeListener() {       
	        @SuppressWarnings("unchecked")
			public void valueChange(ValueChangeEvent event) {
	        	if(staffTable.getValue()==null){
	        		Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s, Specialization spc "
	                		+ "where s.id = h.id and spc.id = s.specid and spc.id = :value1 and h.day = :value2" );               
	                cal.setTime(dateSelect.getValue());
	                
	                query.setParameter("value1", specSelect.getValue());
	                query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
	                
	                hours = query.getResultList();
	        	}
	        	else{
		        	Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s "
		        			+ "where s.id = h.id and s.id = :value1 and h.day = :value2");
		        	cal.setTime(dateSelect.getValue());
	                
		        	query.setParameter("value1", staffTable.getValue());
	                query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
	               
	                hours = query.getResultList();
	        	}
		        generateAppointments();
	        }
	    });
        
        dateSelect.addValueChangeListener(new Property.ValueChangeListener() {	
			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(!dateSelect.isValid() || dateSelect.getValue() == null){
					try {
						if(getMaxDate().before(dateSelect.getValue())){
							dateSelect.setValue(getMaxDate());			
						}
						else{
							dateSelect.setValue(getTodaysDate());
						}
					} catch (NullPointerException e) {
						dateSelect.setValue(getTodaysDate());
					} 
				}
				if(staffTable.getValue()==null){
					Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s, Specialization spc "
	                		+ "where s.id = h.id and spc.id = s.specid and spc.id = :value1 and h.day = :value2" );               
	                cal.setTime(dateSelect.getValue());
	                
	                query.setParameter("value1", specSelect.getValue());
	                query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
	                
	                hours = query.getResultList();
				}
				else{
					Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s "
		        			+ "where s.id = h.id and s.id = :value1 and h.day = :value2");
		        	cal.setTime(dateSelect.getValue());
	                
		        	query.setParameter("value1", staffTable.getValue());
	                query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
	               
	                hours = query.getResultList();
				}
				generateAppointments();
			}
		});      
        appointmentTable.addItemClickListener(new ItemClickListener () {
        	@Override
        	public void itemClick(ItemClickEvent event) {
	        	Item item = appointmentTable.getItem(event.getItemId());
	        	
	        	Appointment appointment = new Appointment();
	        	appointment.setIds((int) item.getItemProperty("ids").getValue());
	        	appointment.setDate((Date) item.getItemProperty("date").getValue());
	        	appointment.setHour((Date) item.getItemProperty("date").getValue());
	        	
	        	ConfirmationWindow.open(appointment);
        	}
        });
        
        staffTable.addItemClickListener(new ItemClickListener () {
        	@Override
        	public void itemClick(ItemClickEvent event) {
        		if (event.getButton() == MouseButton.RIGHT) {
        		       Item item = staffTable.getItem(event.getItemId());
        		       
        		       Staff staff = new Staff();
        		       staff.setName((String) item.getItemProperty("name").getValue());
        		       staff.setSurname((String) item.getItemProperty("surname").getValue());
        		       
        		       Notification notif = new Notification(staff.getName(),
							    staff.getSurname(), Notification.TYPE_ERROR_MESSAGE);
					   notif.setPosition(Position.TOP_CENTER);
					   notif.setDelayMsec(2000);
					   notif.show(Page.getCurrent());
        		    }
        	}
        });
               
        verticalLayout.addComponent(specSelect);
        verticalLayout.addComponent(dateSelect);
        verticalLayout.addComponent(staffTable);
        content.addComponent(verticalLayout);
        content.addComponent(appointmentTable);
        setCompositionRoot(content);
    }
    
    private Date getTodaysDate(){
    	Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private Date getMaxDate(){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(getTodaysDate());
        cal.add(Calendar.DATE, 14);
        return cal.getTime();
    }
    
    private void setHoursTableColumns(){
        hoursTable.setVisibleColumns("day", "hstart", "hend");
        hoursTable.setColumnHeader("day", "Dzień„");
        hoursTable.setColumnHeader("hstart", "Od");
        hoursTable.setColumnHeader("hend", "Do");
        hoursTable.setWidth("50%");
    }
    
    private void setAppointmentsTableColumns(){
        appointmentTable.setVisibleColumns("date");
        appointmentTable.setColumnHeader("date", "Dzień i godzina");
    }
    
    @SuppressWarnings("deprecation")
	private void generateAppointments(){
    	try {
	        BeanItemContainer<Hours> tmp = new BeanItemContainer<Hours>(hours);
	        hoursTable.setContainerDataSource(tmp);
	        setHoursTableColumns();
        } catch (IllegalArgumentException e) {
        	hoursTable.setContainerDataSource(new BeanItemContainer<>(Hours.class));
		}
        setHoursTableColumns();
       
        try {
			appointmentList = h2a(hours, dateSelect.getValue());
			appointmentTable.setContainerDataSource(new BeanItemContainer<Appointment>(appointmentList));
		} catch (IllegalArgumentException e) {
			appointmentTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));

		}
        setAppointmentsTableColumns();
    }
    
    private List<Appointment> h2a(List<Hours> hours, Date date) {
    	EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("mao");
        final EntityManager entitymanager = emfactory.createEntityManager();
        
		List<Appointment> appointments = new ArrayList<Appointment>();
		if(hours != null && date!=null){
			Calendar calendar = Calendar.getInstance();
			for(Hours hour : hours) {
				calendar.setTime(date);
				Calendar tmp1 = Calendar.getInstance();
				Calendar tmp2 = Calendar.getInstance();
				
				tmp1.setTime(hour.getHstart());
				tmp2.setTime(hour.getHend());
				
				calendar.add(Calendar.HOUR_OF_DAY, tmp1.get(Calendar.HOUR_OF_DAY));
				do {		
					Date tmpdate;
					
					Appointment appointment = new Appointment();
					appointment.setIds(hour.getStaff().getId());
					
					tmpdate = calendar.getTime();
					appointment.setDate(tmpdate);
					
					appointment.setHour(tmpdate);
					
					Query query = entitymanager.createQuery( "SELECT a FROM Appointment a "
							+ "WHERE a.ids = :value1 AND a.date = :value2 AND a.hour = :value3");
					query.setParameter("value1", appointment.getIds());
	                query.setParameter("value2", appointment.getDate());
	                query.setParameter("value3", appointment.getHour());
	                try {
						query.getSingleResult();
					} catch (NoResultException e) {
						appointments.add(appointment);
					}
					calendar.add(Calendar.MINUTE, 15);				
				} while(calendar.get(Calendar.HOUR_OF_DAY) < tmp2.get(Calendar.HOUR_OF_DAY));
			}
		}
		entitymanager.close();
		emfactory.close();
		return appointments;
	}
}
