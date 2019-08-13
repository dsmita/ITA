package com.assessment.cloud.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {

	private static final long serialVersionUID = -4494211787177104195L;
	private long id;
	private long invoiceId;
	private long productId;
	private int quantity;
	private double tax;
	private double discount;
	private double price;

	public InvoiceItem() {
	}

	public InvoiceItem(long productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "InvoiceItem [id=" + id + ", invoiceId=" + invoiceId + ", productId=" + productId + ", quantity="
				+ quantity + ", tax=" + tax + ", discount=" + discount + ", price=" + price + "]";
	}

}
