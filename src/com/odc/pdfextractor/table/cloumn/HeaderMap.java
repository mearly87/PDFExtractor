package com.odc.pdfextractor.table.cloumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.odc.pdfextractor.model.StringLocation;


@SuppressWarnings("serial")
public class HeaderMap extends HashMap<String, ColumnHeader> {

	public final static Map<String, ColumnHeader> INSTANCE = new HeaderMap();
	
	    @Override
	    public ColumnHeader get(Object key) {
	      String newKey = key.toString().replaceAll("\\s+", " ").toLowerCase().trim();
	      ColumnHeader result = super.get(newKey);
	      if (result == null)
	    	  return ColumnHeader.UNKOWN;
	      return result;
	    }
	    
	    @Override
	    public boolean containsKey(Object key) {
	      String newKey = key.toString().replaceAll("\\s+", " ").toLowerCase().trim();
	      return super.containsKey(newKey);
	    }
	    
	    private HeaderMap()
	    {
	      put("debits", ColumnHeader.DEBIT);
	      put("debit", ColumnHeader.DEBIT);
	      put("amount subtracted", ColumnHeader.DEBIT);
	      put("amount debited", ColumnHeader.DEBIT);
	      put("subtracted", ColumnHeader.DEBIT);
	      put("withdrwal", ColumnHeader.DEBIT);
	      put("withdrawal", ColumnHeader.DEBIT);
	      put("withdrawals", ColumnHeader.DEBIT);
	      put("card withdrawals", ColumnHeader.DEBIT);
	      put("withdrawals/debits", ColumnHeader.DEBIT);
	      
	      put("credits", ColumnHeader.CREDIT);
	      put("credit", ColumnHeader.CREDIT);
	      put("amount added", ColumnHeader.CREDIT);
	      put("amount credited", ColumnHeader.CREDIT);
	      put("added", ColumnHeader.CREDIT);
	      put("deposits", ColumnHeader.CREDIT);
	      put("deposits/credits", ColumnHeader.CREDIT);
	      
	      put("date", ColumnHeader.DATE);
	      put("date posted", ColumnHeader.DATE);
	      put("posted date", ColumnHeader.DATE);
	      put("dates", ColumnHeader.DATE);
	      put("posting", ColumnHeader.DATE);

	      put("description", ColumnHeader.DESCRIPTION);
	      put("descriptions", ColumnHeader.DESCRIPTION);
	      put("description of transaction", ColumnHeader.DESCRIPTION);
	      put("transaction description", ColumnHeader.DESCRIPTION);
	      
	      put("check", ColumnHeader.CHECK_NUMBER);
	      put("check number", ColumnHeader.CHECK_NUMBER);
	      put("check no.", ColumnHeader.CHECK_NUMBER);
	      put("check#", ColumnHeader.CHECK_NUMBER);
	      put("check #", ColumnHeader.CHECK_NUMBER);
	      
	      put("amount", ColumnHeader.AMOUNT);
	      put("amount ($)", ColumnHeader.AMOUNT);
	      put("amount($)", ColumnHeader.AMOUNT);
	      put("amount(s)", ColumnHeader.AMOUNT);

	      put("balance", ColumnHeader.BALANCE);
	      put("balance ($)", ColumnHeader.BALANCE);
	      put("resulting balance", ColumnHeader.BALANCE);
	      put("ending balance", ColumnHeader.BALANCE);
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
