package com.medicalappointmentsonline.Services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.medicalappointmentsonline.Services.*;

@SuppressWarnings({ "unused", "serial" })
public class EditProfileLayout extends CustomComponent {
	
	private EntityManager entitymanager;
	
	private User user;
	
	private TextField emailField;
	private TextField addressField;
	private TextField cityField;
	private TextField phoneField;

	public EditProfileLayout(){
		FormLayout form = new FormLayout();
		form.setMargin(true);
		
		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "mao" );
	 	entitymanager = emfactory.createEntityManager();
	 	Query query = entitymanager.createQuery( "SELECT u FROM User u where u.email = :value1" );
	 	query.setParameter("value1", String.valueOf(VaadinSession.getCurrent().getAttribute(("user"))));
	 	user = (User) query.getSingleResult();
			
		TextField nameField = new TextField("Imię");
		nameField.setRequired(false);
		nameField.setEnabled(false);
		nameField.setValue(user.getName());
		form.addComponent(nameField);
		
		TextField surnameField = new TextField("Nazwisko");
		surnameField.setRequired(false);
		surnameField.setEnabled(false);
		surnameField.setValue(user.getSurname());
		form.addComponent(surnameField);
		
		TextField sexField = new TextField("Płeć");
		sexField.setRequired(false);
		sexField.setEnabled(false);
		if(user.getMale()){
			sexField.setValue("Mężczyzna");
		}
		else{
			sexField.setValue("Kobieta");
		}
		form.addComponent(sexField);
		
		DateField dateField = new DateField("Data urodzenia");
		dateField.setEnabled(false);
		dateField.setValue(user.getDateofbirth());
		form.addComponent(dateField);		
		
		emailField = new TextField("E-mail");
		emailField.setRequired(true);
		emailField.setEnabled(true);
		emailField.addValidator(new EmailValidator("Niepoprawny adres e-mail"));
		emailField.setValue(user.getEmail());
		form.addComponent(emailField);
		
		addressField = new TextField("Adres");
		addressField.setRequired(false);
		addressField.setEnabled(true);
		addressField.setValue(user.getAddress());
		form.addComponent(addressField);
		
		cityField = new TextField("Miasto");
		cityField.setRequired(false);
		cityField.setEnabled(true);
		cityField.setValue(user.getCity());
		form.addComponent(cityField);
		
		phoneField = new TextField("Numer telefonu");
		phoneField.setRequired(true);
		phoneField.setEnabled(true);
		phoneField.addValidator(new RegexpValidator("[0-9]{9}","Niepoprawny numer telefonu!"));
		phoneField.setValue(user.getPhnum());
		form.addComponent(phoneField);
		
		HorizontalLayout footer = new HorizontalLayout();
		
		Button okButton = new Button("Zastosuj", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(emailField.isValid() && phoneField.isValid() && 
						(user.getPhnum()!=phoneField.getValue() || user.getEmail()!=emailField.getValue()
						|| user.getAddress()!= addressField.getValue() || user.getCity() != cityField.getValue())){
					
					String tmpEmail = (user.getEmail());
					String tmpAddress = (user.getAddress());
					String tmpCity = (user.getCity());
					String tmpPhnum = (user.getPhnum());
					try {				
						entitymanager.getTransaction().begin();
						user.setPhnum(phoneField.getValue());
						user.setEmail(emailField.getValue());
						user.setAddress(addressField.getValue());
						user.setCity(cityField.getValue());
						entitymanager.getTransaction().commit();
						getSession().setAttribute("user", user.getEmail());
						
						Notification success = new Notification(
	                            "Zmieniono pomyślnie");
	                    success.setDelayMsec(2000);
	                    success.setStyleName("bar success small");
	                    success.setPosition(Position.TOP_CENTER);
	                    success.show(Page.getCurrent());
					} catch (RollbackException re) {
						getUI().getNavigator().navigateTo(ProfileView.NAME);
						
						@SuppressWarnings("deprecation")
						Notification notif = new Notification("Uwaga",
							    "Istnieje już inny \nużytkownik o podanych danych", Notification.TYPE_ERROR_MESSAGE);
						notif.setPosition(Position.TOP_CENTER);
						notif.setDelayMsec(2000);
						notif.show(Page.getCurrent());
					}
				}
			}
		});
		okButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		Button cancelButton = new Button("Anuluj", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				emailField.setValue(user.getEmail());
				addressField.setValue(user.getAddress());
				cityField.setValue(user.getCity());
				phoneField.setValue(user.getPhnum());
			}
		});
		Label emptyLabel2 = new Label("");
		emptyLabel2.setWidth("1em");
		
		footer.addComponent(okButton);
		footer.addComponent(emptyLabel2);
		footer.addComponent(cancelButton);
		form.addComponent(footer);
					
		setCompositionRoot(form);
	}
}
