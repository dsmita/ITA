package com.assessment.cloud.util;

import org.springframework.http.HttpStatus;

import com.assessment.cloud.exception.WebException;
import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.model.InvoiceItem;

public class Validator {
	
	public static void validate(Invoice invoice) {
		if (invoice == null) {
			throw new WebException(HttpStatus.BAD_REQUEST, "request body must not be null");
		}
		if (invoice.getCustomerId() <= 0) {
			throw new WebException(HttpStatus.BAD_REQUEST, "customer id not set");
		}
		if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
			throw new WebException(HttpStatus.BAD_REQUEST, "item list cannot be null");
		}
		for (InvoiceItem item : invoice.getItems()) {
			validate(item);
		}
	}

	public static void validate(InvoiceItem invoiceItem) {
		if (invoiceItem.getProductId() <= 0) {
			throw new WebException(HttpStatus.BAD_REQUEST, "item id not set");
		}
		if (invoiceItem.getQuantity() <= 0) {
			throw new WebException(HttpStatus.BAD_REQUEST, "quantity not set");
		}
	}

}
