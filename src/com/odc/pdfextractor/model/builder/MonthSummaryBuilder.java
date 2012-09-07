package com.odc.pdfextractor.model.builder;

import java.util.Calendar;
import java.util.Formatter;

import com.ibm.icu.text.DateFormatSymbols;
import com.odc.pdfextractor.model.MonthSummary;
import com.odc.pdfextractor.model.Transaction.TransactionType;

public class MonthSummaryBuilder {
	
	private int numberOfDebits;
	private int numberOfCredits;
	private int numberOfChecks;
	private int numberOfBalances;
	
	private double totalDebitAmount;
	private double totalCreditAmount;
	private double totalCheckAmount;
	private double averageBalance;
	private double totalBalance;	
	private int month;
	
	Calendar lastDate = null;
	Double lastBalance;
	

	public MonthSummaryBuilder(Integer month) {
		this.month = month;
	}

	public void addToBalance(Double resultingBalance, Calendar date) {
		if ( resultingBalance != null && date != null) {
			if (lastDate != null && lastBalance != null) {
				totalBalance += lastBalance * (date.get(Calendar.DAY_OF_MONTH) - lastDate.get(Calendar.DAY_OF_MONTH));
			}
			lastDate = date;
			lastBalance = resultingBalance;
			numberOfBalances++;
		}
		
	}

	public MonthSummary getMonthSummary() {
		finish();
		return new MonthSummary(this);
	}

	public void addAmount(Double amount, TransactionType type) {
		switch (type) {
		case DEBIT:
			totalDebitAmount+= amount;
			numberOfDebits++;
			break;
		case CREDIT:
			totalCreditAmount+= amount;
			numberOfCredits++;
			break;
		case CHECK:
			totalCheckAmount+= amount;
			numberOfChecks++;
			break;
		}
	}

	public void finish() {
		if (lastDate != null && lastBalance != null) {
			totalBalance += lastBalance * (lastDate.getActualMaximum(Calendar.DAY_OF_MONTH) + 1 - lastDate.get(Calendar.DAY_OF_MONTH));
		}
		averageBalance = totalBalance / lastDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		
	}
	
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		String monthString = new DateFormatSymbols().getMonths()[month];
		Formatter formatter = new Formatter(result);
	    formatter.format("*** %-3.3s *** %n", monthString);
	    formatter.format("%-5.25s %-20.20s%n", "Average Balance: ", averageBalance);
	    formatter.format("%-5.25s %-20.20s%n", "Number Of Debits: ", numberOfDebits);
	    formatter.format("%-5.25s %-20.20s%n", "Total Debits: ", totalDebitAmount);
	    formatter.format("%-5.25s %-20.20s%n", "Number Of Debits: ", numberOfCredits);
	    formatter.format("%-5.25s %-20.20s%n", "Total Debits: ", totalCreditAmount);
	    formatter.format("%-5.25s %-20.20s%n", "Number Of Debits: ", numberOfChecks);
	    formatter.format("%-5.25s %-20.20s%n", "Total Debits: ", totalCheckAmount);
		return result.toString();
	}

	public int getNumberOfDebits() {
		return numberOfDebits;
	}

	public int getNumberOfCredits() {
		return numberOfCredits;
	}

	public int getNumberOfChecks() {
		return numberOfChecks;
	}

	public int getNumberOfBalances() {
		return numberOfBalances;
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
}

