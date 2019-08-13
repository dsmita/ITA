package com.assessment.cloud.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.assessment.cloud.exception.EmailException;
import com.assessment.cloud.model.Invoice;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@Service("emailService")
public class EmailService {

	SendGrid sendGrid;

	@Value("${sendgrid_api_key}")
	private String sendGridApiKey;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@PostConstruct
	public void init() {
		sendGrid = new SendGrid(sendGridApiKey);
	}

	public void sendEmail(Long customerId, Invoice invoice) throws EmailException {
		Request request = setUpEmailContent(invoice);
		Response response = null;
		try {
			response = sendGrid.api(request);
		} catch (IOException e) {
			String message = "Sending email falied for invoice : " + invoice.getId();
			logger.error(message, e);
			throw new EmailException(message, e);
		}
		if (response.getStatusCode() / 100 != 2) {
			String message = "Sending email failed, invoice id : " + invoice.getId() + " response code : "
					+ response.getStatusCode();
			logger.error(message);
			throw new EmailException(message);
		}
	}

	private Request setUpEmailContent(Invoice invoice) throws EmailException {

		Email from = new Email("assessmentTest@abc.com");
		String subject = "Invoice for invoiceId - " + invoice.getId();
		Email to = new Email("debs.sahoo@gmail.com");
		Content content = new Content("text/plain", invoice.toString());
		Mail mail = new Mail(from, subject, to, content);

		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		try {
			request.setBody(mail.build());
		} catch (IOException e) {
			String message = "Error setting up email invoice id: " + invoice.getId();
			logger.error(message, e);
			throw new EmailException(message, e);
		}
		return request;
	}

}
