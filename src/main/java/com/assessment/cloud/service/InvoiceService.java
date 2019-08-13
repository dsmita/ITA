package com.assessment.cloud.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.cloud.dao.InvoiceDao;
import com.assessment.cloud.dao.ProductDao;
import com.assessment.cloud.exception.EmailException;
import com.assessment.cloud.exception.WebException;
import com.assessment.cloud.model.AnalyticsData;
import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.model.InvoiceItem;
import com.assessment.cloud.model.Product;

@Service("invoiceService")
public class InvoiceService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

	@Autowired
	InvoiceDao invoiceDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private EmailService emailService;

	@Value("${taxPercentage}")
	private double taxPercentage;

	@Transactional
	public void create(Invoice invoice) {
		setDetails(invoice);
		invoiceDao.create(invoice);
		List<InvoiceItem> items = invoice.getItems();
		for (InvoiceItem item : items) {
			item.setInvoiceId(invoice.getId());
		}

		invoiceDao.create(items);
	}

	private void setDetails(Invoice invoice) {
		double subTotal = 0;
		double tax = 0;
		double total = 0;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		for (InvoiceItem invoiceItem : invoice.getItems()) {

			Product product = productDao.getById(invoiceItem.getProductId());
			double price = product.getPrice();
			double discount = product.getDiscount();
			invoiceItem.setPrice(price);
			invoiceItem.setDiscount(discount);
			
			int quantity = invoiceItem.getQuantity();
			double itemPrice = price - discount;
			double itemTax = 0.0;
			if(product.isTaxable()) {
				itemTax = itemPrice * taxPercentage / 100;
				invoiceItem.setTax(itemTax);
			}
			double itemTotal = itemPrice + itemTax;
			
			subTotal = subTotal + (itemPrice * quantity);
			tax = tax + (itemTax * quantity);
			total = total + (itemTotal * quantity);
		}
		invoice.setSubTotal(subTotal);
		invoice.setTax(tax);
		invoice.setTotal(total);
		invoice.setCreateTime(now);
		invoice.setUpdateTime(now);
	}

	public Invoice getById(Long id) {
		Invoice invoice = invoiceDao.getById(id);
		if (invoice == null) {
			logger.error("Invoice not found id : " + id);
			throw new WebException(HttpStatus.NOT_FOUND, "Invoice not found for id: " + id);
		}
		invoice.setItems(invoiceDao.getInvoiceItemListByInvoiceId(id));
		return invoice;
	}

	public AnalyticsData getAnalyticsData() {
		AnalyticsData analyticsData = new AnalyticsData();
		analyticsData.setAggregatedAmount(invoiceDao.getTotalInvoiceAmount());
		HashMap<Long, Double> amountByProductId = new HashMap<Long, Double>();
		List<Map<String, Object>> map = invoiceDao.getTotalAmountByProduct();
		for (Map<String, Object> m : map) {
			amountByProductId.put((long) m.get("PRODUCTID"), (double) m.get("TOTAL"));
		}
		analyticsData.setAggregatedAmountByProduct(amountByProductId);
		analyticsData.setTotalInvoicesEmailed(invoiceDao.getTotalInvoicesEmailed());
		return analyticsData;
	}

	@Transactional
	public void sendEmail(Long id) {
		Invoice invoice = getById(id);
		try {
			emailService.sendEmail(invoice.getCustomerId(), invoice);
		} catch (EmailException e) {
			throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		invoiceDao.setEmailSent(id, true);
	}

}
