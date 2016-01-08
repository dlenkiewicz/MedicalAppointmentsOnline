package com.medicalappointmentsonline.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.medicalappointmentsonline.Domain.*;
import com.medicalappointmentsonline.Services.*;
import com.medicalappointmentsonline.MedicalAppointmentsOnline.*;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "unused", "serial" })
public class RegisterWindow extends Window {
	private EntityManagerFactory emfactory;
	private EntityManager entitymanager;
	
	private TextField nameField;
	private TextField surnameField;
	private TextField emailField;
	private TextField addressField;
	private TextField cityField;
	private TextField phoneField;
	
	private OptionGroup sexField;
	
	private PasswordField passwordField;
	
	private DateField dateField;

	@SuppressWarnings("deprecation")
	private RegisterWindow(){
		emfactory = Persistence.createEntityManagerFactory("mao");
	 	entitymanager = emfactory.createEntityManager();
		
		Responsive.makeResponsive(this);
		setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
		addStyleName("profile-window");
		setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);
        
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);
        
        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);
        
        detailsWrapper.addComponent(buildRegisterForm());
        
        content.addComponent(buildFooter());   
	}
	
	private Component buildRegisterForm(){
		HorizontalLayout root = new HorizontalLayout();
		root.setCaption("Uzupełnij dane");
        root.setIcon(FontAwesome.USER);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
		root.addStyleName("profile-form");
		
		FormLayout form = new FormLayout();
        form.setSizeFull();
		form.setMargin(true);
		form.setSpacing(true);;
		
		root.addComponent(form);
        
        emailField = new TextField("E-mail");
		emailField.setRequired(true);
		emailField.setEnabled(true);
		emailField.addValidator(new EmailValidator("Niepoprawny adres e-mail!"));
		emailField.setNullRepresentation("");
		emailField.setInputPrompt("np. jan@kowalski.com");
		form.addComponent(emailField);
		
		passwordField = new PasswordField("Hasło");
		passwordField.setRequired(true);
		passwordField.setEnabled(true);
		passwordField.addValidator(new PasswordValidator());
		passwordField.setNullRepresentation("");
		form.addComponent(passwordField);
		form.addComponent(passwordField);
		
		nameField = new TextField("Imię");
		nameField.setRequired(true);
		nameField.setEnabled(true);
		nameField.setNullRepresentation("");
		nameField.addValidator(new RegexpValidator("[a-zA-Z]+","Podaj imię!"));
		form.addComponent(nameField);
		
		surnameField = new TextField("Nazwisko");
		surnameField.setRequired(true);
		surnameField.setEnabled(true);
		surnameField.setNullRepresentation("");
		surnameField.addValidator(new RegexpValidator("[a-zA-Z]+","Podaj nazwisko!"));
		form.addComponent(surnameField);
		
		sexField = new OptionGroup("Płeć");
		sexField.setRequired(true);
        sexField.addItem(Boolean.FALSE);
        sexField.setItemCaption(Boolean.FALSE, "Kobieta");
        sexField.addItem(Boolean.TRUE);
        sexField.setItemCaption(Boolean.TRUE, "Mężczyzna");
        sexField.setValue(Boolean.FALSE);
        sexField.addStyleName("horizontal");
        form.addComponent(sexField);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        
        dateField = new DateField("Data urodzenia");
		dateField.setEnabled(true);
		dateField.setRequired(true);
		try {
			dateField.addValidator(new DateRangeValidator("Niepoprawna data", sdf.parse("1900.01.01"), 
					sdf.parse("2015.12.30"), Resolution.DAY));
		} catch (ParseException e) {e.printStackTrace();}
		form.addComponent(dateField);	
		
		addressField = new TextField("Adres");
		addressField.setRequired(false);
		addressField.setEnabled(true);
		addressField.setNullRepresentation("");
		addressField.setInputPrompt("np. Kwiatowa 10a/25");
		form.addComponent(addressField);
		
		cityField = new TextField("Miasto");
		cityField.setRequired(false);
		cityField.setEnabled(true);
		cityField.setNullRepresentation("");
		form.addComponent(cityField);
		
		phoneField = new TextField("Numer telefonu");
		phoneField.setRequired(true);
		phoneField.setEnabled(true);
		phoneField.addValidator(new RegexpValidator("[0-9]{9}","Niepoprawny numer telefonu!"));
		phoneField.setNullRepresentation("");
		phoneField.setInputPrompt("np. 123456789");
		form.addComponent(phoneField);	
		
		form.addComponent(new Label("<font color=\"red\">*</font> <font size=\"2\">Wymagane pole</font>",ContentMode.HTML));
		
		return root;
	}
	
	
	private Component buildFooter(){
		HorizontalLayout footer = new HorizontalLayout();
		
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		
		Button okButton = new Button("Wyślij", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(emailField.isValid() && phoneField.isValid() && passwordField.isValid()
						&& nameField.isValid() && surnameField.isValid() && dateField.isValid()){
					try{	
						entitymanager.getTransaction().begin();
						
						User user = new User();
						
						user.setName(nameField.getValue());
						user.setSurname(surnameField.getValue());
						user.setPassword(passwordField.getValue());
						user.setMale(((Boolean) sexField.getValue()).booleanValue());
						user.setPhnum(phoneField.getValue());
						user.setEmail(emailField.getValue());
						user.setAddress(addressField.getValue());
						user.setCity(cityField.getValue());
						user.setDateofbirth(dateField.getValue());
						System.out.println(emailField.getValue());
						
						entitymanager.persist(user);
						entitymanager.getTransaction().commit();
						entitymanager.close();
						emfactory.close();
						close();
						
	                    Notification success = new Notification(
	                            "Zarejestrowano pomyślnie");
	                    success.setDelayMsec(2000);
	                    success.setStyleName("bar success small");
	                    success.setPosition(Position.TOP_CENTER);
	                    success.show(Page.getCurrent());
					}catch(RollbackException re){
						@SuppressWarnings("deprecation")
						Notification notif = new Notification("Uwaga",
							    "Podany użytkownik już istnieje!", Notification.TYPE_ERROR_MESSAGE);
						notif.setPosition(Position.TOP_CENTER);
						notif.setDelayMsec(2000);
						notif.show(Page.getCurrent());
					}
				}
				else{
					@SuppressWarnings("deprecation")
					Notification notif = new Notification("Uwaga",
						    "Uzupełnij wymagane dane!", Notification.TYPE_WARNING_MESSAGE);
					notif.setPosition(Position.TOP_CENTER);
					notif.setDelayMsec(2000);
					notif.show(Page.getCurrent());
				}
			}
		});
		okButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		Button cancelButton = new Button("Zamknij", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {		
				entitymanager.close();
				emfactory.close();
				close();
			}
		});
		
		Label emptyLabel = new Label("");
		emptyLabel.setWidth("1em");
		
		footer.addComponent(new HorizontalLayout(okButton,emptyLabel,cancelButton));	
		return footer;
	}
	
	public static void open() {
	        Window w = new RegisterWindow();
	        UI.getCurrent().addWindow(w);
	        w.focus();
	    }
	
	private static final class PasswordValidator extends AbstractValidator<String> {
		public PasswordValidator() {
		    super("Hasło musi mieć conajmniej 8 znaków w tym minimalnie jedną cyfrę.");
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
}
