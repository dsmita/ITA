package com.assessment.cloud.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assessment.cloud.dao.ProductDao;
import com.assessment.cloud.model.Product;

@Service("productService")
public class ProductService {

	@Autowired
	ProductDao productDao;

	@Transactional
	public void create(Product product) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		product.setCreateTime(now);
		product.setUpdateTime(now);
		productDao.create(product);
	}

	public Product get(Long id) {
		return productDao.getById(id);
	}

}
