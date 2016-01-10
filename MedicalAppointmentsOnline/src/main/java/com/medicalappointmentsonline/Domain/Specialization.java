package com.medicalappointmentsonline.Domain;

import javax.persistence.*;

@Entity
@Table(name="spec")
public class Specialization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="spec")
	private String spec;
	
	public Specialization() {
	}
	
	public Specialization(String spec) {
		this.spec = spec;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
}
