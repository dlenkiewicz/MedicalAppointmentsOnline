package com.medicalappointmentsonline.Domain;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="appointment")
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_u")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="id_s")
	private Staff staff;
	
	@Temporal(TemporalType.DATE)
	@Column(name="date")
	private Date date;
	
	@Temporal(TemporalType.TIME)
	@Column(name="hour")
	private Date hour;
	
	@ManyToOne
	@JoinColumn(name="app_type_id")
	private AppointmentType appointmentType;
	

	public Appointment() {}

	public Appointment(Date date, Date hour) {
		super();
		this.date = date;
		this.hour = hour;
	}

	public int getId() {
		return id;		
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (!user.getAppointments().contains(this)) {
			this.setDate(DateUtils.truncate(this.getDate(), Calendar.DATE));			
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(this.getHour());
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, 0);
			this.setHour(cal.getTime());			
			user.getAppointments().add(this);
        }
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getHour() {
		return hour;
	}

	public void setHour(Date hour) {
		this.hour = hour;
	}
	
	public AppointmentType getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(AppointmentType appointmentType) {
		this.appointmentType = appointmentType;
	}
}
