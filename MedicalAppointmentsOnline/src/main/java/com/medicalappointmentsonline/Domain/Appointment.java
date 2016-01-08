package com.medicalappointmentsonline.Domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="appointment")
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="id_u")
	private int idu;
	
	@Column(name="id_s")
	private int ids;
	
	@Temporal(TemporalType.DATE)
	@Column(name="date")
	private Date date;
	
	@Temporal(TemporalType.TIME)
	@Column(name="hour")
	private Date hour;
	
	public Appointment() {}

	public Appointment(int idu, int ids, Date date, Date hour) {
		this.idu = idu;
		this.ids = ids;
		this.date = date;
		this.hour = hour;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdu() {
		return idu;
	}

	public void setIdu(int idu) {
		this.idu = idu;
	}

	public int getIds() {
		return ids;
	}

	public void setIds(int ids) {
		this.ids = ids;
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
}
