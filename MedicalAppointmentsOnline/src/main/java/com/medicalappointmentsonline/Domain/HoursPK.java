package com.medicalappointmentsonline.Domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@SuppressWarnings("serial")
@Embeddable
public class HoursPK implements Serializable {
	
	@Column(name="id")
	private Integer id;
	
	@Column(name="day")
	private Integer day;
	
	@Temporal(TemporalType.TIME)
	@Column(name="hour_start")
	private Date hstart;
 
    public HoursPK() {
    }
 
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HoursPK){
        	HoursPK hoursPk = (HoursPK) obj;
 
            if(!hoursPk.getId().equals(id)){
                return false;
            }
 
            if(!hoursPk.getDay().equals(day)){
                return false;
            }
            
            if(!hoursPk.getHstart().equals(hstart)){
                return false;
            }
 
            return true;
        }
 
        return false;
    }
 
    @Override
    public int hashCode() {
        return id.hashCode() + day.hashCode() + hstart.hashCode();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
}
