package com.odc.pdfextractor.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.enumeration.HeaderType;
import com.odc.pdfextractor.model.StringLocation;


@SuppressWarnings("serial")
public class HeaderClassifier extends HashMap<String, HeaderType> {

	public final static Map<String, HeaderType> INSTANCE = new HeaderClassifier();
	
	    @Override
	    public HeaderType get(Object key) {
	      String newKey = key.toString().replaceAll("\\s+", " ").toLowerCase().trim();
	      HeaderType result = super.get(newKey);
	      if (result == null)
	    	  return HeaderType.UNKOWN;
	      return result;
	    }
	    
	    @Override
	    public boolean containsKey(Object key) {
	      String newKey = key.toString().replaceAll("\\s+", " ").toLowerCase().trim();
	      return super.containsKey(newKey);
	    }
	    
	    private HeaderClassifier()
	    {
	      put("debits", HeaderType.DEBIT);
	      put("debit", HeaderType.DEBIT);
	      put("amount subtracted", HeaderType.DEBIT);
	      put("amount debited", HeaderType.DEBIT);
	      put("subtracted", HeaderType.DEBIT);
	      put("withdrwal", HeaderType.DEBIT);
	      put("withdrawal", HeaderType.DEBIT);
	      put("withdrawals", HeaderType.DEBIT);
	      put("card withdrawals", HeaderType.DEBIT);
	      put("withdrawals/debits", HeaderType.DEBIT);
	      
	      put("credits", HeaderType.CREDIT);
	      put("credit", HeaderType.CREDIT);
	      put("amount added", HeaderType.CREDIT);
	      put("amount credited", HeaderType.CREDIT);
	      put("added", HeaderType.CREDIT);
	      put("deposits", HeaderType.CREDIT);
	      put("deposits/credits", HeaderType.CREDIT);
	      
	      put("date", HeaderType.DATE);
	      put("date posted", HeaderType.DATE);
	      put("posted date", HeaderType.DATE);
	      put("dates", HeaderType.DATE);
	      put("posting", HeaderType.DATE);
	      put("posting date", HeaderType.DATE);

	      put("description", HeaderType.DESCRIPTION);
	      put("descriptions", HeaderType.DESCRIPTION);
	      put("description of transaction", HeaderType.DESCRIPTION);
	      put("transaction description", HeaderType.DESCRIPTION);
	      
	      put("check", HeaderType.CHECK_NUMBER);
	      put("check number", HeaderType.CHECK_NUMBER);
	      put("check no.", HeaderType.CHECK_NUMBER);
	      put("check#", HeaderType.CHECK_NUMBER);
	      put("check #", HeaderType.CHECK_NUMBER);
	      
	      put("amount", HeaderType.AMOUNT);
	      put("amount ($)", HeaderType.AMOUNT);
	      put("amount($)", HeaderType.AMOUNT);
	      put("amount(s)", HeaderType.AMOUNT);

	      put("balance", HeaderType.BALANCE);
	      put("balance ($)", HeaderType.BALANCE);
	      put("resulting balance", HeaderType.BALANCE);
	      put("ending balance", HeaderType.BALANCE);
	    };

	public static List<StringLocation> getValidHeaders(List<StringLocation> headers) {
		List<StringLocation> validHeaders = new ArrayList<StringLocation>();
		for (StringLocation header : headers) {
			if(INSTANCE.containsKey(header)) {
				validHeaders.add(header);
			}
		}
		return validHeaders;
	}
}
