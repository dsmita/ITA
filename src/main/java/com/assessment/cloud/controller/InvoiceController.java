package com.assessment.cloud.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.cloud.model.AnalyticsData;
import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.service.InvoiceService;
import com.assessment.cloud.util.Validator;

@RestController
@RequestMapping("/v1/invoices")
public class InvoiceController {

	@Autowired
	InvoiceService invoiceService;

	private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Invoice> create(@RequestBody Invoice invoice) {
		Validator.validate(invoice);
		invoiceService.create(invoice);
		logger.info("Invoice created for id : " + invoice.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
	}

	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Invoice> get(@PathVariable(value = "id") Long id) {
		Invoice invoice = invoiceService.getById(id);
		logger.info("Invoice fetched for id : " + id);
		return ResponseEntity.status(HttpStatus.OK).body(invoice);
	}

	@RequestMapping(path = "{id}/sendEmail", method = RequestMethod.PUT)
	public ResponseEntity<String> sendEmail(@PathVariable(value = "id") Long id) {
		invoiceService.sendEmail(id);
		logger.info("Invoice email sent for id : " + id);
		return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");
	}

	@RequestMapping(path = "/analytics", method = RequestMethod.GET)
	public ResponseEntity<AnalyticsData> getAnalyticsData() {
		AnalyticsData data = invoiceService.getAnalyticsData();
		return ResponseEntity.status(HttpStatus.OK).body(data);
	}

}
