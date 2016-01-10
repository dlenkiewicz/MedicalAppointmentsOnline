package com.medicalappointmentsonline.Domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name="staff_spec")
public class StaffSpecialization {
	@EmbeddedId
    private StaffSpecializationPK staffSpecializationPK;
	
	@ManyToOne
	@MapsId("staffId")
	@JoinColumn(name="staff_id")
	private Staff staff;
	
	@ManyToOne
	@MapsId("specId")
	@JoinColumn(name="spec_id")
	private Specialization specialization;
	
	public StaffSpecialization() {
	}
	
	public StaffSpecialization(Specialization specialization, Staff staff){
		super();
		this.staff = staff;
		this.specialization = specialization;
	}

	public StaffSpecializationPK getStaffSpecializationPK() {
		return staffSpecializationPK;
	}

	public void setStaffSpecializationPK(StaffSpecializationPK staffSpecializationPK) {
		this.staffSpecializationPK = staffSpecializationPK;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}
}
