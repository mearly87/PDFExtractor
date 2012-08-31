package com.odc.pdfextractor.transaction;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.odc.pdfextractor.model.StringLocation;
import com.odc.pdfextractor.transaction.TableClassifier.TableType;
import com.odc.pdfextractor.transaction.Transaction.TransactionType;

public class TransactionBuilder
{

  public enum ColumnHeader {
    DATE(new DateHandler()),
    CHECK_NUMBER(new CheckNumberHandler()),
    DEBIT(new DebitHandler()),
    CREDIT(new CreditHandler()),
    BALANCE(new BalanceHandler()),
    DESCRIPTION(new DescriptionHandler()), 
    AMOUNT(new AmountHandler()),
    UNKOWN(new UnknownHandler());
    ColumnHandler handler;
    
    ColumnHeader(ColumnHandler handler) {
      this.handler = handler;
    }
  }
  
  public static Transaction getTransaction(Map<StringLocation, StringLocation> dataMap) throws ParseException {
    
    Transaction trans = new Transaction();
    Set<StringLocation> keys = dataMap.keySet();
    TableType type = TableClassifier.getTableType(dataMap.get(StringLocation.TABLE_HEADER).toString());
    if (type == TableType.CREDIT) {
    	trans.setType(TransactionType.CREDIT);
    } else if (type == TableType.DEBIT) {
    	trans.setType(TransactionType.DEBIT);
    } else if (type == TableType.BALANCE) {
    	trans.setType(TransactionType.BALANCE);
    }	else if (type == TableType.CHECK) {
    	trans.setType(TransactionType.CHECK);
    }
    for (StringLocation key : keys) {
      if (key == null) {
        continue;
      }
      if(keywordMap.containsKey(key)) {
        ColumnHandler handler = keywordMap.get(key).handler;
        if(dataMap.get(key) != null) {
          handler.handleColumn(trans, key.toString().trim().toLowerCase(), dataMap.get(key).toString().trim());
        }
      }
    }
    if (trans.getType() == null) {
      trans.setType(TransactionType.UNKNOWN);
    }
    System.out.println(trans);
    return trans;
  }
  
  @SuppressWarnings("serial")
  private static Map<String, ColumnHeader> keywordMap = new HashMap<String, ColumnHeader>() {
    @Override
    public ColumnHeader get(Object key) {
      return super.get(key.toString().toLowerCase().trim());
    }
    
    @Override
    public boolean containsKey(Object key) {
      return super.containsKey(key.toString().toLowerCase().trim());
    }
    
    {
      put("debits", ColumnHeader.DEBIT);
      put("debit", ColumnHeader.DEBIT);
      put("amount subtracted", ColumnHeader.DEBIT);
      put("amount debited", ColumnHeader.DEBIT);
      put("subtracted", ColumnHeader.DEBIT);
      put("withdrawls", ColumnHeader.DEBIT);
      put("withdrawals/debits", ColumnHeader.DEBIT);
      
      put("credits", ColumnHeader.CREDIT);
      put("credit", ColumnHeader.CREDIT);
      put("amount added", ColumnHeader.CREDIT);
      put("amount credited", ColumnHeader.CREDIT);
      put("added", ColumnHeader.CREDIT);
      put("desposits", ColumnHeader.CREDIT);
      put("deposits/credits", ColumnHeader.CREDIT);
      
      put("date", ColumnHeader.DATE);
      put("date posted", ColumnHeader.DATE);
      put("posted date", ColumnHeader.DATE);
      put("dates", ColumnHeader.DATE);

      put("description", ColumnHeader.DESCRIPTION);
      put("descriptions", ColumnHeader.DESCRIPTION);
      
      put("check", ColumnHeader.CHECK_NUMBER);
      put("check number", ColumnHeader.CHECK_NUMBER);
      put("check no.", ColumnHeader.CHECK_NUMBER);
      put("check#", ColumnHeader.CHECK_NUMBER);
      put("check #", ColumnHeader.CHECK_NUMBER);
      
      put("amount", ColumnHeader.AMOUNT);
      put("amount ($)", ColumnHeader.AMOUNT);
      put("amount($)", ColumnHeader.AMOUNT);
      
      
      put("balance", ColumnHeader.BALANCE);
      put("balance ($)", ColumnHeader.BALANCE);
      put("resulting balance", ColumnHeader.BALANCE);
    }
    };
  
  
  
  
  
  
}
