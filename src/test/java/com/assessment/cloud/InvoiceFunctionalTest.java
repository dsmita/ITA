package com.assessment.cloud;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.assessment.cloud.model.AnalyticsData;
import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.model.InvoiceItem;
import com.assessment.cloud.model.Product;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class InvoiceFunctionalTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${taxPercentage}")
	private double taxPercentage;

	private DBUtil dbUtil;

	@PostConstruct
	public void beforeClass() {
		this.dbUtil = new DBUtil(jdbcTemplate);
	}

	@Before
	public void before() {
		dbUtil.deleteTable("invoice_item");
		dbUtil.deleteTable("invoice");
		dbUtil.deleteTable("product");
	}

	@Test
	public void createInvoice() throws Exception {
		Product p1 = new Product("Apple", 1.89, .9, false);
		Product p2 = new Product("Orange", 2.89, 0.0, false);
		Product p3 = new Product("ToothBrush", 1.0, 0.0, true);
		dbUtil.create(p1, p2, p3);

		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		items.add(new InvoiceItem(p1.getId(), 5));
		items.add(new InvoiceItem(p2.getId(), 2));
		items.add(new InvoiceItem(p3.getId(), 2));

		Invoice invoice = new Invoice();
		invoice.setCustomerId(1L);
		invoice.setItems(items);

		ResponseEntity<Invoice> response = restTemplate.postForEntity("/v1/invoices", invoice, Invoice.class);
		assertEquals(response.toString(), HttpStatus.CREATED, response.getStatusCode());

		Invoice actual = response.getBody();
		assertTrue(actual.getId() > 0);
		assertTrue((4.95 + 5.78 + 2) == actual.getSubTotal());
		assertTrue(0.2 == actual.getTax());
		assertTrue((4.95 + 5.78 + 2.2) == actual.getTotal());
		assertNotNull(actual.getCreateTime());
		assertNotNull(actual.getUpdateTime());
		List<InvoiceItem> actualItems = actual.getItems();
		assertTrue(actualItems.size() == 3);
		assertInvoiceItem(actualItems.get(0), p1);
		assertInvoiceItem(actualItems.get(1), p2);
		assertInvoiceItem(actualItems.get(2), p3);

		Invoice fromDb = dbUtil.getInvoiceById(actual.getId());
		assertTrue(fromDb.getId() == actual.getId());
		assertTrue(fromDb.getSubTotal() == actual.getSubTotal());
		assertTrue(fromDb.getTax() == actual.getTax());
		assertTrue(fromDb.getTotal() == actual.getTotal());

		List<InvoiceItem> itemsFromDb = dbUtil.getItemsByInvoiceId(actual.getId());
		assertTrue(itemsFromDb.size() == 3);
		assertInvoiceItem(itemsFromDb.get(0), p1);
		assertInvoiceItem(itemsFromDb.get(1), p2);
		assertInvoiceItem(itemsFromDb.get(2), p3);
	}
	
	@Test
	public void getById() {
		Product p1 = new Product("Apple", 1.89, .9, false);
		Product p2 = new Product("Orange", 2.89, 0.0, false);
		Product p3 = new Product("ToothBrush", 5.0, 0.0, true);
		dbUtil.create(p1, p2, p3);

		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		items.add(new InvoiceItem(p1.getId(), 5));
		items.add(new InvoiceItem(p2.getId(), 2));
		items.add(new InvoiceItem(p3.getId(), 1));

		Invoice invoice = new Invoice();
		invoice.setCustomerId(1L);
		invoice.setItems(items);
		setInvoiceDetails(invoice);
		dbUtil.createInvoice(invoice);
		for (InvoiceItem item : items) {
			item.setInvoiceId(invoice.getId());
		}
		dbUtil.createInvoiceItems(items);
		
		ResponseEntity<Invoice> response = restTemplate.getForEntity("/v1/invoices/{id}", Invoice.class,
				invoice.getId());
		assertEquals(200, response.getStatusCode().value());
		Invoice actualInvoice = response.getBody();
		assertEquals(invoice.getId(), actualInvoice.getId());
		assertEquals(invoice.getSubTotal(), actualInvoice.getSubTotal(),
				invoice.getSubTotal() - actualInvoice.getSubTotal());
		assertEquals(invoice.getTax(), actualInvoice.getTax(), invoice.getTax() - actualInvoice.getTax());
		assertEquals(invoice.getTotal(), actualInvoice.getTotal(), invoice.getTotal() - actualInvoice.getTotal());
		
		List<InvoiceItem> actualitems = actualInvoice.getItems();
		assertTrue(actualitems.size() == 3);
		assertInvoiceItem(actualitems.get(0), p1);
		assertInvoiceItem(actualitems.get(1), p2);
		assertInvoiceItem(actualitems.get(2), p3);
	}

	@Test
	public void sendEmail() throws Exception {
		Product p1 = new Product("Apple", 1.89, .9, false);
		Product p2 = new Product("Orange", 2.89, 0.0, false);
		Product p3 = new Product("ToothBrush", 5.0, 0.0, true);
		dbUtil.create(p1, p2, p3);

		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		items.add(new InvoiceItem(p1.getId(), 5));
		items.add(new InvoiceItem(p2.getId(), 2));
		items.add(new InvoiceItem(p3.getId(), 1));

		Invoice invoice = new Invoice();
		invoice.setCustomerId(1L);
		invoice.setItems(items);
		setInvoiceDetails(invoice);
		dbUtil.createInvoice(invoice);

		assertNotNull(invoice.getId());

		for (InvoiceItem item : items) {
			item.setInvoiceId(invoice.getId());
		}

		dbUtil.createInvoiceItems(items);

		String url = "/v1/invoices/{id}/sendEmail";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class,
				invoice.getId());
		assertEquals(response.toString(), HttpStatus.OK, response.getStatusCode());

		Invoice fromDb = dbUtil.getInvoiceById(invoice.getId());
		assertTrue(fromDb.isEmailSent());
	}

	@Test
	public void getAnalytics() throws Exception {
		Product p1 = new Product("Apple", 1.89, .9, false);
		Product p2 = new Product("Orange", 2.89, 0.0, false);
		Product p3 = new Product("ToothBrush", 5.0, 0.0, true);
		dbUtil.create(p1, p2, p3);
		createInvoiceAndItems(p1, p2, p3, 1L);
		createInvoiceAndItems(p1, p2, p3, 2L);

		ResponseEntity<AnalyticsData> response = restTemplate.getForEntity("/v1/invoices/analytics",
				AnalyticsData.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		AnalyticsData analyticsData = response.getBody();
		assertTrue(analyticsData.getAggregatedAmount() == (9.899999999999999 + 11.56 + 33));
		Map<Long, Double> aggregatedAmountByProduct = analyticsData.getAggregatedAmountByProduct();

		Map<Long, Double> expected = new HashMap<>();
		expected.put(p1.getId(), 9.899999999999999);
		expected.put(p2.getId(), 11.56);
		expected.put(p3.getId(), 33.0);

		assertThat(aggregatedAmountByProduct.entrySet(), equalTo(expected.entrySet()));
	}

	@Test
	public void getInvoiceWithNoItems() {
		Invoice invoice = new Invoice();
		invoice.setCustomerId(1l);
		invoice.setSubTotal(0.0);
		invoice.setTax(0.0);
		invoice.setTotal(0.0);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		invoice.setCreateTime(now);
		invoice.setUpdateTime(now);

		dbUtil.createInvoice(invoice);

		ResponseEntity<Invoice> response = restTemplate.getForEntity("/v1/invoices/{id}", Invoice.class,
				invoice.getId());
		assertEquals(200, response.getStatusCode().value());
		Invoice actualInvoice = response.getBody();
		assertEquals(invoice.getId(), actualInvoice.getId());
		assertEquals(invoice.getSubTotal(), actualInvoice.getSubTotal(),
				invoice.getSubTotal() - actualInvoice.getSubTotal());
		assertEquals(invoice.getTax(), actualInvoice.getTax(), invoice.getTax() - actualInvoice.getTax());
		assertEquals(invoice.getTotal(), actualInvoice.getTotal(), invoice.getTotal() - actualInvoice.getTotal());
		assertEquals(Collections.emptyList(), actualInvoice.getItems());
	}

	@Test
	public void getByInvalidId() {
		int invalidId = Integer.MAX_VALUE;
		Product p1 = new Product("Apple", invalidId, 0.0, false);
		Product p2 = new Product("Orange", 2, 0.0, false);
		Product p3 = new Product("ToothBrush", 5.0, 0.0, true);
		dbUtil.create(p1, p2, p3);
		createInvoiceAndItems(p1, p2, p3, 1L);

		ResponseEntity<Invoice> response = restTemplate.getForEntity("/v1/invoices/{id}", Invoice.class, invalidId);
		assertEquals(404, response.getStatusCode().value());
	}

	private Invoice createInvoiceAndItems(Product p1, Product p2, Product p3, Long CustomerId) {
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		items.add(new InvoiceItem(p1.getId(), 5));
		items.add(new InvoiceItem(p2.getId(), 2));
		items.add(new InvoiceItem(p3.getId(), 3));

		Invoice invoice = new Invoice();
		invoice.setCustomerId(CustomerId);
		invoice.setItems(items);
		setInvoiceDetails(invoice);
		dbUtil.createInvoice(invoice);

		assertNotNull(invoice.getId());

		for (InvoiceItem item : items) {
			item.setInvoiceId(invoice.getId());
		}

		dbUtil.createInvoiceItems(items);
		return invoice;
	}

	private void setInvoiceDetails(Invoice invoice) {
		double subTotal = 0;
		double tax = 0;
		double total = 0;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		for (InvoiceItem invoiceItem : invoice.getItems()) {

			Product product = dbUtil.getProductById(invoiceItem.getProductId());
			double price = product.getPrice();
			double discount = product.getDiscount();
			invoiceItem.setPrice(price);
			invoiceItem.setDiscount(discount);

			int quantity = invoiceItem.getQuantity();
			double itemPrice = price - discount;
			double itemTax = 0.0;
			if (product.isTaxable()) {
				itemTax = itemPrice * taxPercentage / 100;
			}
			invoiceItem.setTax(itemTax);
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

	private void assertInvoiceItem(InvoiceItem item, Product product) {
		assertTrue(item.getProductId() == product.getId());
		assertTrue(item.getPrice() == product.getPrice());
		assertTrue(item.getDiscount() == product.getDiscount());
		assertTrue(item.getTax() == (product.isTaxable()
				? (product.getPrice() - product.getDiscount()) * taxPercentage / 100 : 0.0));

	}

}
