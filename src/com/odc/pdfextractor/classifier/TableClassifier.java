package com.odc.pdfextractor.classifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.odc.pdfextractor.enumeration.HeaderType;
import com.odc.pdfextractor.enumeration.TableType;


public class TableClassifier {
	

	
	public static TableType getTableType (String tableHeader, Set<HeaderType> headerTypes) {
		Set<TableType> possibleTypes = new HashSet<TableType>();
		for (HeaderType word : headerTypes) {
			if (tableMap.containsKey(word.toString())) {
				possibleTypes.add(tableMap.get(word));
			}
		}
		for (String word : tableHeader.split(" ")) {
			if (tableMap.containsKey(word)) {
				possibleTypes.add(tableMap.get(word));
			}
		}
		if (possibleTypes.isEmpty()) {
			return TableType.UNKNOWN;
		}
		if (possibleTypes.size() == 1) {
			return possibleTypes.iterator().next();
		} 
		if (possibleTypes.contains(TableType.CHECK)) {
			return TableType.CHECK;
		}

		possibleTypes.remove(TableType.BALANCE);
		
		if(possibleTypes.size() == 1) {
			return possibleTypes.iterator().next();
		}
		return TableType.DEBIT_CREDIT;
	}
	
		@SuppressWarnings("serial")
	  private static Map<String, TableType> tableMap = new HashMap<String, TableType>() {
	    @Override
	    public TableType get(Object key) {
	      return super.get(key.toString().toLowerCase().trim());
	    }
	    
	    @Override
	    public boolean containsKey(Object key) {
	      return super.containsKey(key.toString().toLowerCase().trim());
	    }
	    
	    {
	      put("debits", TableType.DEBIT);
	      put("debit", TableType.DEBIT);
	      put("amount subtracted", TableType.DEBIT);
	      put("amount debited", TableType.DEBIT);
	      put("subtracted", TableType.DEBIT);
	      put("withdrawals", TableType.DEBIT);
	      put("fees", TableType.DEBIT);
	      put("withdrawals/debits", TableType.DEBIT);
	      put("payments", TableType.DEBIT);
	      put("service charges", TableType.DEBIT);
	      put("charges", TableType.DEBIT);
	      
	      put("credits", TableType.CREDIT);
	      put("credit", TableType.CREDIT);
	      put("amount added", TableType.CREDIT);
	      put("amount credited", TableType.CREDIT);
	      put("added", TableType.CREDIT);
	      put("deposits", TableType.CREDIT);
	      put("additions", TableType.CREDIT);
	      put("deposits/credits", TableType.CREDIT);
	      
	      put("check", TableType.CHECK);
	      put("check number", TableType.CHECK);
	      put("check no.", TableType.CHECK);
	      put("check#", TableType.CHECK);
	      put("check #", TableType.CHECK);
	      put("check_number", TableType.CHECK);
	      
	      
	      put("balance", TableType.BALANCE);
	      put("balance ($)", TableType.BALANCE);
	      put("resulting balance", TableType.BALANCE);
	    }
	    };
	

}
