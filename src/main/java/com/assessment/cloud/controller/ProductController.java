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

import com.assessment.cloud.exception.WebException;
import com.assessment.cloud.model.Product;
import com.assessment.cloud.service.ProductService;

@RestController
@RequestMapping("/v1/products")
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Product> create(@RequestBody Product product) {
		productService.create(product);
		logger.info("Product created for id :" + product.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}
	
	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Product> get(@PathVariable(value = "id") Long id) {
		Product product = productService.get(id);
		if (product == null) {
			logger.error("Product not found id : "+ id);
			throw new WebException(HttpStatus.NOT_FOUND, "Product not found for id: " + id);
		}
		logger.info("Product fetched for id : "+ id);
		return ResponseEntity.status(HttpStatus.OK).body(product);
	}

}
