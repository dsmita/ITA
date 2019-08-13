package com.assessment.cloud;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.model.InvoiceItem;
import com.assessment.cloud.model.Product;

public class DBUtil {

	private JdbcTemplate jdbcTemplate;

	public DBUtil(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void deleteTable(String table) {
		String sql = String.format("DELETE FROM %s", table);
		jdbcTemplate.execute(sql);
	}

	public void dropTable(String table) {
		String sql = String.format("DROP TABLE %s", table);
		jdbcTemplate.execute(sql);
	}

	public Invoice getInvoiceById(long id) {
		String sql = "SELECT * FROM invoice WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Invoice>(Invoice.class), id);
	}

	public List<InvoiceItem> getItemsByInvoiceId(long invoiceId) {
		String sql = "SELECT * FROM invoice_item WHERE invoiceId = ? ORDER BY productId";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<InvoiceItem>(InvoiceItem.class), invoiceId);
	}

	public Product getProductById(long id) {
		String sql = "SELECT * FROM product WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Product>(Product.class), id);
	}

	public void setEmailSent(long id) {
		String sql = "UPDATE invoice SET emailSent = ? WHERE id = ?";
		jdbcTemplate.update(sql, true, id);
	}

	public void create(Product... products) {
		final String sql = "INSERT INTO product(name, price, discount, taxable, createTime, updateTime) VALUES (?, ?, ?, ?, ?, ?)";
		final Date now = new Date(System.currentTimeMillis());
		for (final Product product : products) {
			final PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
					final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, product.getName());
					ps.setDouble(2, product.getPrice());
					ps.setDouble(3, product.getDiscount());
					ps.setBoolean(4, product.isTaxable());
					ps.setDate(5, now);
					ps.setDate(6, now);
					return ps;
				}
			};

			final KeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(psc, holder);
			product.setId(holder.getKey().longValue());
		}
	}
	
	public void createInvoice(final Invoice invoice) {

		final String sql = "INSERT INTO invoice(customerId, subtotal, tax, total, createTime, updateTime) VALUES (?, ?, ?, ?, ?, ?)";

		final PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, invoice.getCustomerId());
				ps.setDouble(2, invoice.getSubTotal());
				ps.setDouble(3, invoice.getTax());
				ps.setDouble(4, invoice.getTotal());
				ps.setTimestamp(5, invoice.getCreateTime());
				ps.setTimestamp(6, invoice.getUpdateTime());
				return ps;
			}
		};

		final KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, holder);

		invoice.setId(holder.getKey().longValue());
	}
	
	public int[][] createInvoiceItems(List<InvoiceItem> items) {
		final String sql = "INSERT INTO invoice_item(invoiceId, productId, quantity, price, discount, tax ) VALUES (?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.batchUpdate(sql, items, 10, new ParameterizedPreparedStatementSetter<InvoiceItem>() {
			public void setValues(PreparedStatement ps, InvoiceItem item) throws SQLException {
				ps.setLong(1, item.getInvoiceId());
				ps.setLong(2, item.getProductId());
				ps.setInt(3, item.getQuantity());
				ps.setDouble(4, item.getPrice());
				ps.setDouble(5, item.getDiscount());
				ps.setDouble(6, item.getTax());
			}
		});
	}
	
	public List<Invoice> getInvoice() {
		String sql = "SELECT * FROM invoice";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Invoice>(Invoice.class));
	}


}
