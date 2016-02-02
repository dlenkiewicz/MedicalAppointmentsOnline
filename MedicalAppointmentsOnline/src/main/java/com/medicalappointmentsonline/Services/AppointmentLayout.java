package com.medicalappointmentsonline.Services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;

@SuppressWarnings("serial")
public class AppointmentLayout extends CustomComponent {

	private EntityManagerFactory emfactory;
	private EntityManager entitymanager;
	
	private Table staffTable;
    private Table appointmentTable;
    
    private ComboBox specSelect;
    private ComboBox appTypeSelect;
    
    private DateField dateSelect;
    
    private JPAContainer<Specialization> specs;
    private JPAContainer<AppointmentType> appointmentTypes;
    
    private List<Staff> staff;
    private List<Hours> hours;
    private List<Appointment> appointmentList;
    
    private boolean disableListener;
    
    @SuppressWarnings({ "deprecation", "unchecked", "unused" })
	public AppointmentLayout(){
    	emfactory = Persistence.createEntityManagerFactory( "mao" );
        entitymanager = emfactory.createEntityManager();
    	
    	HorizontalLayout content = new HorizontalLayout();
        content.setMargin(true);
        content.setSpacing(true);
       
        VerticalLayout verticalLayout = new VerticalLayout();
        
        specs = JPAContainerFactory.make(Specialization.class,
                AppointmentsUI.PERSISTENCE_UNIT);
        appointmentTypes = JPAContainerFactory.make(AppointmentType.class,
        		AppointmentsUI.PERSISTENCE_UNIT);

        Calendar cal = Calendar.getInstance();
        
        dateSelect = new DateField("Wybierz dzień");
        dateSelect.addValidator(new DateRangeValidator("Podana data jest spoza możliwych terminów przyjęć", 
        		getTodaysDate(), getMaxDate(), Resolution.DAY));
        dateSelect.setValue(getTodaysDate());
        
        Query query2 = entitymanager.createQuery("SELECT s FROM Staff s");
        staff = query2.getResultList();      
        staffTable = new Table("Wybierz lekarza:");
        staffTable.setSelectable(true);
        generateStaffTable();
       
        specSelect = new ComboBox("Wybierz specjalistę", specs);
        specSelect.setItemCaptionPropertyId("spec");
        specSelect.setNullSelectionAllowed(false);
        specSelect.setFilteringMode(FilteringMode.CONTAINS);
        
        appTypeSelect = new ComboBox("Wybierz typ wizyty", appointmentTypes);
        appTypeSelect.setItemCaptionPropertyId("type");
        appTypeSelect.setNullSelectionAllowed(false);
        appTypeSelect.setFilteringMode(FilteringMode.CONTAINS);
                
        appointmentTable = new Table("Wybierz termin wizyty:");
        appointmentTable.setImmediate(true);
        appointmentTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class));
        appointmentTable.setSelectable(true);
        appointmentTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        appointmentTable.setWidth("70%");
        setAppointmentsTableColumns();
        
        disableListener = false;
        
        specSelect.addValueChangeListener(new Property.ValueChangeListener() {
        	Filter filter = null;
        	@Override
        	public void valueChange(ValueChangeEvent event) {
        		if(disableListener == false){
	        		if (specSelect.getValue() != null) {
			        	if (filter != null)
			        		appointmentTypes.removeContainerFilter(filter);
			        	filter = new Compare.Equal("specialization.id", specSelect.getValue());
			        	appointmentTypes.addContainerFilter(filter);
			        }  	 
	        		disableListener = true;
	        		Query query2 = entitymanager.createQuery( "SELECT a FROM Specialization s, AppointmentType a "
	                		+ "WHERE s.id = :value1 AND s.id = a.specialization.id");
	                query2.setParameter("value1", specSelect.getValue());
	                query2.setMaxResults(1);
	                AppointmentType appTypeTmp = (AppointmentType) query2.getResultList().get(0);     
	                appTypeSelect.setValue(appTypeTmp.getId());
		        			        			        		
	                executeQueryStaffNull();
	                
	                Query query3 = entitymanager.createQuery("SELECT s FROM Staff s, AppointmentType a, Hours h "
	                		+ "WHERE a.id = h.appointmentType.id AND h.staff.id = s.id AND a.id = :value1");
	                query3.setParameter("value1", appTypeSelect.getValue());
	                staff = query3.getResultList();
	                generateStaffTable();

	                generateAppointments();
        		}
        		
        		disableListener = false;
        		
            }
        });
        
        staffTable.addValueChangeListener(new Property.ValueChangeListener() {  
        	@Override
	        public void valueChange(ValueChangeEvent event) {
	        	        		
        		if(staffTable.getValue()==null){
	        		executeQueryStaffNull();
	        	}
	        	else{
	        		if(specSelect.getValue()==null){
	        			Object tmp = staffTable.getValue();
		        		Query query = entitymanager.createQuery("SELECT sS FROM StaffSpecialization sS, Staff s, Hours h, AppointmentType a "
								+ "WHERE s.id = :value1 AND sS.staff.id = s.id AND "
								+ "sS.specialization.id = a.specialization.id AND "
								+ "a.id = h.appointmentType.id AND s.id = h.staff.id");
		        		query.setParameter("value1",((Staff) staffTable.getValue()).getId());
		        		query.setMaxResults(1);
		        		List<StaffSpecialization> staffList = query.getResultList();
		        		if(staffList.size()!=0){
		        			StaffSpecialization staffSpec = staffList.get(0);
		        			specSelect.setValue(staffSpec.getSpecialization().getId());
		        			staffTable.setValue(tmp);
		        		}
		        		else{
		        			Notification notif = new Notification("Uwaga",
								    "Wybrany lekarz aktualnie nie przeprowadza żadnych wizyt!", Notification.TYPE_ERROR_MESSAGE);
							notif.setPosition(Position.TOP_CENTER);
							notif.setDelayMsec(2000);
							notif.show(Page.getCurrent());
		        		}
		        				
		        	}
	        		executeQueryStaffNotNull();
	        	}
		        generateAppointments();
	        }
	    });
        
        appTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {    	 	
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(disableListener==false){
					disableListener = true;
					Query query2 = entitymanager.createQuery("SELECT a FROM AppointmentType a "
							+ "where a.id = :value1");
					query2.setParameter("value1", appTypeSelect.getValue());
					query2.setMaxResults(1);
					AppointmentType tmpAppType = (AppointmentType) query2.getResultList().get(0);					
					specSelect.setValue(tmpAppType.getSpecialization().getId());
					if(staffTable.getValue()==null){
						executeQueryStaffNull();
					}
					else {
						executeQueryStaffNotNull();
					}
					Query query3 = entitymanager.createQuery("SELECT s FROM Staff s, AppointmentType a, Hours h "
	                		+ "WHERE a.id = h.appointmentType.id AND h.staff.id = s.id AND a.id = :value1");
	                query3.setParameter("value1", appTypeSelect.getValue());
	                staff = query3.getResultList();
	                generateStaffTable();
					generateAppointments();
				}				
				disableListener = false;
			}
		});
        
        dateSelect.addValueChangeListener(new Property.ValueChangeListener() {	
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(!dateSelect.isValid() || dateSelect.getValue() == null){
						
					if(dateSelect.getValue() == null){
						dateSelect.setValue(getTodaysDate());
					}
					else if(getMaxDate().before(dateSelect.getValue())){
						dateSelect.setValue(getMaxDate());			
					}
					else{
						dateSelect.setValue(getTodaysDate());
					}					 
				}
				if(staffTable.getValue()==null){
					executeQueryStaffNull();
				}
				else{
					executeQueryStaffNotNull();
				}				
				generateAppointments();
			}
		});      
        appointmentTable.addItemClickListener(new ItemClickListener () {
        	@Override
        	public void itemClick(ItemClickEvent event) {
	        	Item item = appointmentTable.getItem(event.getItemId());
	        	Appointment appointment = new Appointment();
	        	appointment.setStaff((Staff) item.getItemProperty("staff").getValue());
	        	appointment.setDate((Date) item.getItemProperty("date").getValue());
	        	appointment.setHour((Date) item.getItemProperty("date").getValue());
	        	appointment.setAppointmentType((AppointmentType) item.getItemProperty("appointmentType").getValue());
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
        verticalLayout.addComponent(appTypeSelect);
        verticalLayout.addComponent(dateSelect);
        verticalLayout.addComponent(staffTable);
        content.addComponent(verticalLayout);
        content.addComponent(appointmentTable);
        setCompositionRoot(content);
    }
    
    @SuppressWarnings("unchecked")
	private void executeQueryStaffNull(){
    	Calendar cal = Calendar.getInstance();
    	
    	Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s, StaffSpecialization staffspc "
        		+ "where s.id = h.staff.id and staffspc.staff.id = s.id "
        		+ "and staffspc.specialization.id = :value1 and h.day = :value2 "
        		+ "and h.appointmentType.id = :value3" );               
        cal.setTime(dateSelect.getValue());
        
        query.setParameter("value1", specSelect.getValue());
        query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
        query.setParameter("value3", appTypeSelect.getValue());
        hours = query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	private void executeQueryStaffNotNull(){
    	Calendar cal = Calendar.getInstance();
    	
    	Query query = entitymanager.createQuery( "SELECT h FROM Hours h, Staff s "
    			+ "where s.id = h.staff.id and s.id = :value1 and h.day = :value2 "
    			+ "and h.appointmentType.id = :value3");
    	cal.setTime(dateSelect.getValue());
    	
        query.setParameter("value1",((Staff) staffTable.getValue()).getId());
        query.setParameter("value2", cal.get(Calendar.DAY_OF_WEEK));
        query.setParameter("value3", appTypeSelect.getValue());
        hours = query.getResultList();
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
    
    public Date getMaxDate(){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(getTodaysDate());
        cal.add(Calendar.DATE, 14);
        return cal.getTime();
    }
    
	private void generateStaffTable(){
    	
		BeanItemContainer<Staff> tmp = new BeanItemContainer<>(Staff.class, staff);
		staffTable.setContainerDataSource(tmp);
		
        staffTable.setVisibleColumns("name", "surname");
        staffTable.setColumnHeader("name", "Imię");
        staffTable.setColumnHeader("surname", "Nazwisko");
    }
    
    private void setAppointmentsTableColumns(){
        appointmentTable.setVisibleColumns("date", "staff");
        appointmentTable.setColumnHeader("date", "Dzień i godzina");
        appointmentTable.setColumnHeader("staff", "Lekarz");
    }
    
	private void generateAppointments(){       
        
		appointmentList = h2a(hours, dateSelect.getValue());
		appointmentTable.setContainerDataSource(new BeanItemContainer<>(Appointment.class, appointmentList));
		
        setAppointmentsTableColumns();
    }
    
    private List<Appointment> h2a(List<Hours> hours, Date date) {
    	EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "mao" );
        final EntityManager entitymanager = emfactory.createEntityManager();
		List<Appointment> appointments = new ArrayList<Appointment>();
		if(hours != null && date!=null){
			Calendar calendar = Calendar.getInstance();
			for(Hours hour : hours) {
				int appointmentTimeLength = hour.getAppointmentType().getTimeLength();
				
				calendar.setTime(date);
				
				Calendar startTime = Calendar.getInstance();
				Calendar endTime = Calendar.getInstance();
				Calendar currentTime = Calendar.getInstance();
				
				currentTime.setTime(new Date());				
				startTime.setTime(hour.getHstart());
				endTime.setTime(hour.getHend());
				
				if(date.equals(getTodaysDate())){
					if(currentTime.get(Calendar.HOUR_OF_DAY)>startTime.get(Calendar.HOUR_OF_DAY)){
						if(currentTime.get(Calendar.HOUR_OF_DAY)>endTime.get(Calendar.HOUR_OF_DAY)){
							continue;
						} else if (currentTime.get(Calendar.HOUR_OF_DAY)==endTime.get(Calendar.HOUR_OF_DAY)){
							if(currentTime.get(Calendar.MINUTE)>endTime.get(Calendar.MINUTE)-appointmentTimeLength){
								continue;
							} else {
								calendar.add(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
								calendar.add(Calendar.MINUTE, (currentTime.get(Calendar.MINUTE)/appointmentTimeLength + 1)*appointmentTimeLength);
							}
						} else {
							calendar.add(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
							calendar.add(Calendar.MINUTE, (currentTime.get(Calendar.MINUTE)/appointmentTimeLength + 1)*appointmentTimeLength);
						}
					} else if(currentTime.get(Calendar.HOUR_OF_DAY) == startTime.get(Calendar.HOUR_OF_DAY)){
						calendar.add(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
						if (currentTime.get(Calendar.MINUTE)>startTime.get(Calendar.MINUTE)){
							calendar.add(Calendar.MINUTE, (currentTime.get(Calendar.MINUTE)/appointmentTimeLength + 1)*appointmentTimeLength);
						} else {
							calendar.add(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
						}
					} else {
						calendar.add(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
						calendar.add(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
					}		
				} else {
					calendar.add(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
					calendar.add(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
				}
				while(calendar.get(Calendar.HOUR_OF_DAY) < endTime.get(Calendar.HOUR_OF_DAY) || 
						(calendar.get(Calendar.HOUR_OF_DAY) == endTime.get(Calendar.HOUR_OF_DAY) && 
							calendar.get(Calendar.MINUTE) < endTime.get(Calendar.MINUTE))) {		
					Date tmpdate;
					Appointment appointment = new Appointment();
					appointment.setStaff(hour.getStaff());
					appointment.setAppointmentType(hour.getAppointmentType());
					tmpdate = calendar.getTime();
					appointment.setHour(tmpdate);
					appointment.setDate(tmpdate);
					
					Query query = entitymanager.createQuery( "SELECT a FROM Appointment a "
							+ "WHERE a.staff.id = :value1 AND a.date = :value2 AND a.hour = :value3");
					query.setParameter("value1", appointment.getStaff().getId());
	                query.setParameter("value2", appointment.getDate());
	                query.setParameter("value3", appointment.getHour());
	                if(query.getResultList().size() == 0) {
						appointments.add(appointment);
					}
					calendar.add(Calendar.MINUTE, hour.getAppointmentType().getTimeLength());				
				}
			}
		}
		entitymanager.close();
		emfactory.close();
		return appointments;
	}
}
