package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.Services.*;

@SuppressWarnings("serial")
public class LoginView extends CustomComponent implements View,
Button.ClickListener {
	
	public static final String NAME = "login";
	
	private final TextField user;
	
	private final PasswordField password;
	
	private final Button loginButton;
	
	private final Button registerButton;
	
	public LoginView() {
		setSizeFull();
		
		user = new TextField("User:");
		user.setWidth("300px");
		user.setRequired(true);
		user.setInputPrompt("Twój email - np. jan@kowalski.com");
		user.addValidator(new EmailValidator("Nazwa użytkownika musi być adresem e-mail"));
		user.setInvalidAllowed(false);
		
		password = new PasswordField("Password:");
		password.setWidth("300px");
		password.addValidator(new PasswordValidator());
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");
		
		loginButton = new Button("Zaloguj", this);
		
		registerButton = new Button("Zarejestruj", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {				
				RegisterWindow.open();
			}
		});
		
		HorizontalLayout footer = new HorizontalLayout();
		Label emptyLabel2 = new Label("");
		emptyLabel2.setWidth("1em");
		
		footer.addComponent(loginButton);
		footer.addComponent(emptyLabel2);
		footer.addComponent(registerButton);
		
		FormLayout fields = new FormLayout(user, password, footer);
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();
		
		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		setCompositionRoot(viewLayout);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		user.focus();
	}
	
	private static final class PasswordValidator extends
	    AbstractValidator<String> {
	
		public PasswordValidator() {
		    super("Wpisane hasło ma zły format");
		}
		
		@Override
		protected boolean isValidValue(String value) {
		    if (value != null
		            && (value.length() < 8 || !value.matches(".*\\d.*"))) {
		        return false;
		    }
		    return true;
		}
		
		@Override
		public Class<String> getType() {
		    return String.class;
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	
		if (!user.isValid() || !password.isValid()) {
		    return;
		}
		
		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory( "mao" );
		EntityManager entitymanager = emfactory.createEntityManager();
		
		String username = user.getValue();
		String password = this.password.getValue();
		try{
			Query query = entitymanager.createQuery( "SELECT u FROM User u where u.email = :value1" );
			query.setParameter("value1", username);
			User user = (User) query.getSingleResult();
			
			boolean isValid = username.equals(user.getEmail()) && password.equals(user.getPassword());
			
			if (isValid) {
			    getSession().setAttribute("user", username);
			    getUI().getNavigator().navigateTo(AppointmentsMainView.NAME);//
			
			} 
			
			else {
				@SuppressWarnings("deprecation")
				Notification notif = new Notification("Uwaga",
					    "Niepoprawne hasło!", Notification.TYPE_ERROR_MESSAGE);
				notif.setPosition(Position.TOP_CENTER);
				notif.setDelayMsec(2000);
				notif.show(Page.getCurrent());
			    this.password.setValue(null);
			    this.password.focus();
			}
		}catch (NoResultException wyjatek) {
			@SuppressWarnings("deprecation")
			Notification notif = new Notification("Uwaga",
				    "Wpisana nazwa użytkownika nie istnieje!", Notification.TYPE_ERROR_MESSAGE);
			notif.setPosition(Position.TOP_CENTER);
			notif.setDelayMsec(2000);
			notif.show(Page.getCurrent());
			
		} finally {
			entitymanager.close();
		    emfactory.close();
		}
	}
}