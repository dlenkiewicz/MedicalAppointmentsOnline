package com.medicalappointmentsonline.Domain;

import java.util.List;

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
	
	@OneToMany(mappedBy="staff", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Hours> hours;

	@Override public String toString() {
	return this.getName()+" "+this.getSurname();
	}
	
	public Staff() {
	}

	public Staff(String name, String surname) {
		this.name = name;
		this.surname = surname;
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
	
	public List<Hours> getHours() {
		return hours;
	}

	public void setHours(List<Hours> hours) {
		this.hours = hours;
	}
}
