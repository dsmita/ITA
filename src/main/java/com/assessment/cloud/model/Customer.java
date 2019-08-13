package com.assessment.cloud.model;

import java.io.Serializable;

public class Customer implements Serializable{
	
	private static final long serialVersionUID = -7056009286977771228L;
	private long id;
	private String name;
	private Address address;
	private String email;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
