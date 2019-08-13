package com.assessment.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.assessment.cloud.model.Product;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ProductFunctionalTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

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
	public void createProduct() throws Exception {
		Product product = new Product();
		product.setName("One");
		product.setPrice(10);
		product.setTaxable(true);

		ResponseEntity<Product> response = restTemplate.postForEntity("/v1/products", product, Product.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		Product created = response.getBody();
		assertTrue(created.getId() > 0);
		assertProduct(product, created);

		Product fromDb = dbUtil.getProductById(created.getId());
		assertProduct(created, fromDb);
		assertEquals(created.getId(), fromDb.getId());
	}

	private void assertProduct(Product expected, Product actual) {
		assertEquals(expected.getName(), actual.getName());
		assertTrue(expected.getPrice() == actual.getPrice());
		assertEquals(expected.isTaxable(), actual.isTaxable());
		assertNotNull(actual.getCreateTime());
		assertNotNull(actual.getUpdateTime());
	}

}
