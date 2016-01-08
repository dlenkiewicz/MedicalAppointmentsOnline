package com.medicalappointmentsonline.Domain;

import javax.persistence.*;

@Entity
@Table(name="staff")
public class Staff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name", length=50)
	private String name;
	
	@Column(name="surname", length=50)
	private String surname;
	
	@Column(name="spec_id")
	private int specid;
	
	public Staff() {}

	public Staff(String name, String surname, int specid) {
		this.name = name;
		this.surname = surname;
		this.specid = specid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getSpecid() {
		return specid;
	}

	public void setSpecid(int specid) {
		this.specid = specid;
	}
}
