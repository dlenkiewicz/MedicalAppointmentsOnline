package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


@SuppressWarnings("serial")
public class MenuView extends CustomComponent {
	public MenuView(){
		setPrimaryStyleName("valo-menu");
		setSizeUndefined();
		setCompositionRoot(buildContent());
	}
	
	private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }
	
	 private Component buildTitle() {
		 	Label logo = new Label("<strong>Witaj!</strong>", ContentMode.HTML);
		 	logo.setSizeUndefined();
	        
		 	VerticalLayout logoWrapper = new VerticalLayout(logo);
	        
		 	logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
	        logoWrapper.addStyleName("valo-menu-title");
	        
	        return logoWrapper;
	    }
	 
	 private Component buildMenuItems() {
		 CssLayout menuItemsLayout = new CssLayout();
	     menuItemsLayout.addStyleName("valo-menuitems");
	     
	     menuItemsLayout.addComponent(new AppointmentButton());
	     menuItemsLayout.addComponent(new HistoryButton());
	     menuItemsLayout.addComponent(new ProfileButton());
	     menuItemsLayout.addComponent(new LogoutButton());
	     
	     return menuItemsLayout;
	 }
	 
	 public class HistoryButton extends Button { 
		 public HistoryButton(){
			 setPrimaryStyleName("valo-menu-item");
			 setCaption("Moje wizyty");
			 setIcon(FontAwesome.BRIEFCASE);
			 
			 addClickListener(new ClickListener() {
				  @Override
	              public void buttonClick(final ClickEvent event) {
					  getUI().getNavigator().navigateTo("history");
				  }
			 });
		 }
	 }
	 
	 public class LogoutButton extends Button { 
		 public LogoutButton(){
			 setPrimaryStyleName("valo-menu-item");
			 setCaption("Wyloguj");
			 setIcon(FontAwesome.SIGN_OUT);
			 
			 addClickListener(new ClickListener() {
				  @Override
	              public void buttonClick(final ClickEvent event) {
					  getSession().setAttribute("user", null);
					  getUI().getNavigator().navigateTo("");
				  }
			 });
		 }
	 }
	 
	 public class ProfileButton extends Button { 
		 public ProfileButton(){
			 setPrimaryStyleName("valo-menu-item");
			 setCaption("Profil");
			 setIcon(FontAwesome.INFO);
			 
			 addClickListener(new ClickListener() {
				  @Override
	              public void buttonClick(final ClickEvent event) {
					  getUI().getNavigator().navigateTo(ProfileView.NAME);
				  }
			 });
		 }
	 }
	 
	 public class AppointmentButton extends Button { 
		 public AppointmentButton(){
			 setPrimaryStyleName("valo-menu-item");
			 setCaption("Umów wizytę");
			 setIcon(FontAwesome.ARCHIVE);
			 
			 addClickListener(new ClickListener() {
				  @Override
	              public void buttonClick(final ClickEvent event) {
					  getUI().getNavigator().navigateTo(AppointmentsMainView.NAME);
				  }
			 });
		 }
	 }	 
}
