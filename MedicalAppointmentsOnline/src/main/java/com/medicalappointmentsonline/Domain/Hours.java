package com.medicalappointmentsonline.Domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="hours")
public class Hours {
	@EmbeddedId
    private HoursPK hoursPK;
	
	@ManyToOne
	@MapsId("id")
	@JoinColumn(name="id")
	private Staff staff;
	
	@Column(name="day")
	private Integer day;
	
	@Temporal(TemporalType.TIME)
	@Column(name="hour_start")
	private Date hstart;
	
	@Temporal(TemporalType.TIME)
	@Column(name="hour_end")
	private Date hend;
	
	@ManyToOne
	@JoinColumn(name="app_type_id")
	private AppointmentType appointmentType;
	
	public Hours() {}

	public Hours(HoursPK hoursPK, Integer day, Date hstart, Date hend) {
		super();
		this.hoursPK = hoursPK;
		this.day = day;
		this.hstart = hstart;
		this.hend = hend;
	}

	public HoursPK getHoursPK() {
		return hoursPK;
	}

	public void setHoursPK(HoursPK hoursPK) {
		this.hoursPK = hoursPK;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Date getHstart() {
		return hstart;
	}

	public void setHstart(Date hstart) {
		this.hstart = hstart;
	}

	public Date getHend() {
		return hend;
	}

	public void setHend(Date hend) {
		this.hend = hend;
	}
	
	public AppointmentType getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(AppointmentType appointmentType) {
		this.appointmentType = appointmentType;
	}
}
