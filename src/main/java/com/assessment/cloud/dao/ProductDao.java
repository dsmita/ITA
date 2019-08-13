package com.assessment.cloud.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.assessment.cloud.model.Product;

@Repository
public class ProductDao {

	private static final Logger logger = LoggerFactory.getLogger(ProductDao.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public void create(final Product product) {

		final String sql = "INSERT INTO product(name, price, discount, taxable, createTime, updateTime) VALUES (?, ?, ?, ?, ?, ?)";

		final PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getName());
				ps.setDouble(2, product.getPrice());
				ps.setDouble(3, product.getDiscount());
				ps.setBoolean(4, product.isTaxable());
				ps.setTimestamp(5, product.getCreateTime());
				ps.setTimestamp(6, product.getUpdateTime());
				return ps;
			}
		};

		final KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, holder);

		product.setId(holder.getKey().longValue());
	}

	public Product getById(long id) {
		Product product = null;
		try {
			final String sql = "SELECT * FROM product WHERE id = ?";
			product = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Product>(Product.class), id);
		} catch (EmptyResultDataAccessException e) {
			 logger.error("Product not found for id: {}", id);
		}
		return product;
	}

}
