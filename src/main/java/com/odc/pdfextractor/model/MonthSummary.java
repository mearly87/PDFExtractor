package com.odc.pdfextractor.model;

import java.util.Formatter;

import com.odc.pdfextractor.Constants;
import com.odc.pdfextractor.model.builder.MonthSummaryBuilder;

public class MonthSummary {
	
	private int numberOfDebits;
	private int numberOfCredits;
	private int numberOfChecks;
	private int numberOfBalances;
	
	private final double totalDebitAmount;
	private final double totalCreditAmount;
	private final double totalCheckAmount;
	private final double averageBalance;
	private final int month;
	
	public MonthSummary(MonthSummaryBuilder builder) {
		numberOfDebits = builder.getNumberOfDebits();
		numberOfCredits = builder.getNumberOfCredits();
		numberOfChecks = builder.getNumberOfChecks();
		numberOfBalances = builder.getNumberOfBalances();
			
		totalDebitAmount = builder.getTotalDebitAmount();
		totalCreditAmount = builder.getTotalCreditAmount();
		totalCheckAmount = builder.getTotalCheckAmount();
		averageBalance = builder.getAverageBalance();
		month = builder.getMonth();
	}
	
	
	public int getNumberOfDebits() {
		return numberOfDebits;
	}


	public void setNumberOfDebits(int numberOfDebits) {
		this.numberOfDebits = numberOfDebits;
	}


	public int getNumberOfCredits() {
		return numberOfCredits;
	}


	public void setNumberOfCredits(int numberOfCredits) {
		this.numberOfCredits = numberOfCredits;
	}


	public int getNumberOfChecks() {
		return numberOfChecks;
	}


	public void setNumberOfChecks(int numberOfChecks) {
		this.numberOfChecks = numberOfChecks;
	}


	public int getNumberOfBalances() {
		return numberOfBalances;
	}


	public void setNumberOfBalances(int numberOfBalances) {
		this.numberOfBalances = numberOfBalances;
	}


	public double getTotalDebitAmount() {
		return totalDebitAmount;
	}


	public double getTotalCreditAmount() {
		return totalCreditAmount;
	}


	public double getTotalCheckAmount() {
		return totalCheckAmount;
	}


	public double getAverageBalance() {
		return averageBalance;
	}


	public int getMonth() {
		return month;
	}


	public String toString() {
		if (month == -1)
			return "";
		StringBuilder result = new StringBuilder();
		String monthString = Constants.MONTHS[month];
		Formatter formatter = new Formatter(result);
		result.append("\n################################\n");
	    formatter.format("%-13s %-7.3s %s%n", "##########", monthString, "##########");
		result.append("################################\n\n");
	    formatter.format("%-20s %s%(.2f%n", "Average Balance: ", "$", averageBalance);
	    formatter.format("%-20s %d%n", "Number Of Debits: ", numberOfDebits);
	    formatter.format("%-20s %s%(.2f%n", "Total Debits: ", "$", totalDebitAmount);
	    formatter.format("%-20s %d%n", "Number Of Credits: ", numberOfCredits);
	    formatter.format("%-20s %s%(.2f%n", "Total Credits: ", "$", totalCreditAmount);
	    formatter.format("%-20s %d%n", "Number Of Checks: ", numberOfChecks);
	    formatter.format("%-20s %s%(.2f%n", "Total Checks: ", "$", totalCheckAmount);
		return result.toString();
	}

	public boolean equals(MonthSummary summary) {
		return month == summary.month && Math.abs(averageBalance - summary.averageBalance) < .01 &&
				numberOfDebits == summary.numberOfDebits && Math.abs(totalDebitAmount- summary.totalDebitAmount) < .01 &&
						numberOfCredits == summary.numberOfCredits && Math.abs(totalCreditAmount - summary.totalCreditAmount) < .01 &&
								numberOfChecks == summary.numberOfChecks && Math.abs(totalCheckAmount - summary.totalCheckAmount) < .01;
	}
}
