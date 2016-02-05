package com.medicalappointmentsonline.MedicalAppointmentsOnline;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.UI;

/**
 *
 */

@SuppressWarnings("serial")
@Theme("MAOtheme")
@Widgetset("com.medicalappointmentsonline.MedicalAppointmentsOnline.MAOwidgetset")
@StyleSheet({"https://fonts.googleapis.com/css?family=Source+Sans+Pro"})
public class AppointmentsUI extends UI {
	public static final String PERSISTENCE_UNIT = "mao";

    @Override
    protected void init(VaadinRequest request) {
    	Responsive.makeResponsive(this);
    	addStyleName(ValoTheme.UI_WITH_MENU);

        new Navigator(this, this);
        
        getNavigator().addView(LoginView.NAME, LoginView.class);
        getNavigator().addView(AppointmentsMainView.NAME, AppointmentsMainView.class);
        getNavigator().addView(ProfileView.NAME, ProfileView.class);
        getNavigator().addView(HistoryView.NAME, HistoryView.class);
        
        getNavigator().addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof LoginView;

                if (!isLoggedIn && !isLoginView) {
                	getNavigator().navigateTo(LoginView.NAME);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    return false;
                }
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
            }  
        });
    }   
}



