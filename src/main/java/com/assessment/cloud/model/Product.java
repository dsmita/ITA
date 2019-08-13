package com.assessment.cloud.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Product implements Serializable{

	private static final long serialVersionUID = 8413417833269109781L;
	private long id;
	private String name;
	private double price;
	private double discount;
	private boolean taxable;
	private Timestamp createTime;
	private Timestamp updateTime;
	
	public Product(){}
	
	public Product(String name, double price, double discount, boolean taxable) {
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.taxable = taxable;
	}
	
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public boolean isTaxable() {
		return taxable;
	}
	public void setTaxable(boolean taxable) {
		this.taxable = taxable;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
}
