package com.assessment.cloud.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.assessment.cloud.model.Invoice;
import com.assessment.cloud.model.InvoiceItem;

@Repository
public class InvoiceDao {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceDao.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void create(final Invoice invoice) {

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

	public int[][] create(List<InvoiceItem> items) {
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

	public Invoice getById(Long id) {
		Invoice invoice = null;
		try {
			String sql = "SELECT * FROM invoice WHERE id = ?";
			invoice = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Invoice>(Invoice.class), id);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Invoice not found for id : {}", id);
		}
		return invoice;
	}

	public void setEmailSent(long id, boolean emailSent) {
		String sql = "UPDATE invoice SET emailSent = ? WHERE id = ?";
		jdbcTemplate.update(sql, emailSent, id);
	}

	public double getTotalInvoiceAmount() {
		String sql = "SELECT sum(total) FROM invoice";
		return jdbcTemplate.queryForObject(sql, Double.class);
	}

	public List<Map<String, Object>> getTotalAmountByProduct() {
		String sql = "SELECT productId, sum((price-discount+tax)*quantity) as total FROM invoice_item group by productId";
		return jdbcTemplate.queryForList(sql);
	}

	public long getTotalInvoicesEmailed() {
		String sql = "SELECT count(*) FROM invoice WHERE emailSent is TRUE";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	public List<InvoiceItem> getInvoiceItemListByInvoiceId(Long invoiceId) {
		String sql = "SELECT * FROM invoice_item WHERE invoiceId = ? ";
		return jdbcTemplate.query(sql, new Object[] { invoiceId },
				new BeanPropertyRowMapper<InvoiceItem>(InvoiceItem.class));
	}

}
