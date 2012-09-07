package com.odc.pdfextractor.model.builder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.icu.text.DateFormatSymbols;
import com.odc.pdfextractor.model.MonthSummary;
import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;

public class SummaryBuilder {
	
	private List<Transaction> transactions;
	
	public SummaryBuilder(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void createSummary() {
	    SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM");
	    Map<Integer, List<Transaction>> transByMonth = groupByMonth(
				transactions, simpleDateformat);
	    Map<Integer, MonthSummaryBuilder> summaries = getMonthSummaryMap(transByMonth.keySet());
	    Collections.sort(transactions);
    	for (Transaction trans : transactions) {
    		if (trans.getDate() == null) {
    			System.out.println("Null Date for trans: " + trans);
    			continue;
    		}
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(trans.getDate());
	    	int month = cal.get(Calendar.MONTH);
	    	MonthSummaryBuilder summary = summaries.get(month);
	    	if (trans.getResultingBalance() != null ) {
	    		cal = Calendar.getInstance();
	    		cal.setTime(trans.getDate());
	    		summary.addToBalance(trans.getResultingBalance(), cal);
	    	}
	    	if (trans.getAmount() != null) {
	    		summary.addAmount(trans.getAmount(), trans.getType());
	    	}
    	}
    	List<MonthSummary> monthSummaries = new ArrayList<MonthSummary>();
    	for(MonthSummaryBuilder summary : summaries.values()) {
    		MonthSummary monthSummary = summary.getMonthSummary();
    		System.out.println(monthSummary);
    		monthSummaries.add(monthSummary);
    	}
	}

	private  Map<Integer, List<Transaction>> groupByMonth(
			List<Transaction> transactions, SimpleDateFormat simpleDateformat) {
		Map<Integer, List<Transaction>> transByMonth = new HashMap<Integer,  List<Transaction>>();
	    for (Transaction trans : transactions) {
	    	System.out.println(trans);
	    	if (trans.getDate() != null) {
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime(trans.getDate());
		    	int month = cal.get(Calendar.MONTH);
		    	if (!transByMonth.containsKey(month)) {
		    		transByMonth.put(month, new ArrayList<Transaction>());
		    	}
		    	transByMonth.get(month).add(trans);
	    	}
	    }
		return transByMonth;
	}
	
	private  Map<Integer, MonthSummaryBuilder> getMonthSummaryMap(Set<Integer> months) {
		Map<Integer, MonthSummaryBuilder> summaries = new HashMap<Integer, MonthSummaryBuilder>();
		for (Integer month : months) {
			MonthSummaryBuilder summary = new MonthSummaryBuilder(month);
			summaries.put(month, summary);
		}
		return summaries;
	}
	
	
	
}
