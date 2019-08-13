package com.assessment.cloud.model;

import java.util.Map;

public class AnalyticsData {
	
	private Double aggregatedAmount;
	private Map<Long, Double> aggregatedAmountByProduct;
	private Long totalInvoicesEmailed;
	
	public Double getAggregatedAmount() {
		return aggregatedAmount;
	}
	public void setAggregatedAmount(Double aggregatedAmount) {
		this.aggregatedAmount = aggregatedAmount;
	}
	public Map<Long, Double> getAggregatedAmountByProduct() {
		return aggregatedAmountByProduct;
	}
	public void setAggregatedAmountByProduct(Map<Long, Double> aggregatedAmountByProduct) {
		this.aggregatedAmountByProduct = aggregatedAmountByProduct;
	}
	public Long getTotalInvoicesEmailed() {
		return totalInvoicesEmailed;
	}
	public void setTotalInvoicesEmailed(Long totalInvoicesEmailed) {
		this.totalInvoicesEmailed = totalInvoicesEmailed;
	}

}
