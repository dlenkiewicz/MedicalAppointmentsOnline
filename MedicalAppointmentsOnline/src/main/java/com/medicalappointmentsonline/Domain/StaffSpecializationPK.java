package com.medicalappointmentsonline.Domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class StaffSpecializationPK implements Serializable {

	@Column(name="spec_id")
	private Integer specId;
	
	@Column(name="staff_id")
	private Integer staffId;
	
	public StaffSpecializationPK() {
	}
	
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof StaffSpecializationPK){
        	StaffSpecializationPK staffSpecializationPK = (StaffSpecializationPK) obj;
            if(!staffSpecializationPK.getSpecId().equals(specId)){
                return false;
            }
 
            if(!staffSpecializationPK.getStaffId().equals(staffId)){
                return false;
            }
            return true;
        } 
        return false;
    }
	
	@Override
    public int hashCode() {
        return specId.hashCode() + staffId.hashCode();
    }

	public Integer getSpecId() {
		return specId;
	}

	public void setSpecId(Integer specId) {
		this.specId = specId;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}
}
