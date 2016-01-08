
package com.medicalappointmentsonline.Domain;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")	
	private int id;
	
	@Column(name="name", length=50)	
	private String name;
	
	@Column(name="surname", length=50)
	private String surname;
	
	@Column(name="is_male")
	private boolean male;
	
	@Temporal(TemporalType.DATE)
	@Column(name="date_of_birth")
	private Date dateofbirth;
	
	@Column(name="email", length=50)
	private String email;
	
	@Column(name="street_address", length=50)
	private String address;
	
	@Column(name="city", length=50)
	private String city;
	
	@Column(name="phone_number", length=50)
	private String phnum;
	
	@Column(name="password", length=50)
	private String password;
	
	public User() {}
	
	public User(String name, String surname, boolean male, Date dateofbirth, String email, String address, String city,
			String phnum, String password) {
		this.name = name;
		this.surname = surname;
		this.male = male;
		this.email = email;
		this.address = address;
		this.city = city;
		this.phnum = phnum;
		this.password = password;
		this.dateofbirth = dateofbirth;
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

	public boolean getMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhnum() {
		return phnum;
	}

	public void setPhnum(String phnum) {
		this.phnum = phnum;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getDateofbirth(){
		return dateofbirth;
	}
	public void setDateofbirth(Date dateofbirth){
		this.dateofbirth = dateofbirth;
	}
}

