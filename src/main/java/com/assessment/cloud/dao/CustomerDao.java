package com.assessment.cloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.assessment.cloud.model.Customer;

@Repository
public class CustomerDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public Customer getById(Long id){
		String sql = "SELECT * FROM customer WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Customer>(Customer.class), id);
	}
}
