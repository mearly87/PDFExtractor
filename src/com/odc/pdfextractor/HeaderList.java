package com.odc.pdfextractor;

import java.util.ArrayList;
import java.util.List;

import com.odc.pdfextractor.model.StringLocation;


@SuppressWarnings("serial")
public class HeaderList extends ArrayList<String> {

	private final static List<String> INSTANCE = new HeaderList();
	
	@Override
	public boolean contains(Object s) {
		if (s == null) {
			return false;
		}
		return super.contains(s.toString().trim().toLowerCase());
	}
	
	private HeaderList() {
	      add("debits");
	      add("debit");
	      add("amount subtracted");
	      add("amount debited");
	      add("subtracted");
	      add("withdrawls");
	      add("withdrawals/debits");
	      
	      add("credits");
	      add("credit");
	      add("amount added");
	      add("amount credited");
	      add("added");
	      add("desposits");
	      add("deposits/credits");
	      
	      add("date");
	      add("date posted");
	      add("posted date");
	      add("dates");

	      add("description");
	      add("descriptions");
	      
	      add("check");
	      add("check number");
	      add("check no.");
	      add("check#");
	      add("check #");
	      
	      add("amount");
	      add("amount ($)");
	      add("amount($)");
	      
	      
	      add("balance");
	      add("balance ($)");
	      add("resulting balance");
	}

	public static List<String> getHeaders() {
		return INSTANCE;
		
	}

	public static List<StringLocation> getValidHeaders(List<StringLocation> headers) {
		List<StringLocation> validHeaders = new ArrayList<StringLocation>();
		for (StringLocation header : headers) {
			if(INSTANCE.contains(header)) {
				validHeaders.add(header);
			}
		}
		return validHeaders;
	}
}
