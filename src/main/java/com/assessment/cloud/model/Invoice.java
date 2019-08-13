package com.assessment.cloud.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Invoice implements Serializable {

	private static final long serialVersionUID = -3962720212413175608L;
	private long id;
	private long customerId;
	private double subTotal;
	private double tax;
	private double total;
	private Timestamp createTime;
	private Timestamp updateTime;
	private boolean emailSent;
	private List<InvoiceItem> items;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
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

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", customerId=" + customerId + ", subTotal=" + subTotal + ", tax=" + tax
				+ ", total=" + total + ", createTime=" + createTime + ", updateTime=" + updateTime + ", emailSent="
				+ emailSent + ", items=" + items + "]";
	}


}
