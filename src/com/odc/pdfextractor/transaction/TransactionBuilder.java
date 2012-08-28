package com.odc.pdfextractor.transaction;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.odc.pdfextractor.model.StringLocation;
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
    AMOUNT(new AmountHandler());
    
    ColumnHandler handler;
    
    ColumnHeader(ColumnHandler handler) {
      this.handler = handler;
    }
  }
  
  public static Transaction getTransaction(Map<StringLocation, StringLocation> dataMap) throws ParseException {
    
    Transaction trans = new Transaction();
    Set<StringLocation> keys = dataMap.keySet();
    for (StringLocation key : keys) {
      if (key == null) {
        continue;
      }
      if(keywordMap.containsKey(key)) {
        ColumnHandler handler = keywordMap.get(key).handler;
        if(dataMap.get(key) != null) {
          handler.handleColumn(trans, key.toString().trim().toLowerCase(), dataMap.get(key).toString().trim());
        }
        else {
          System.out.println(key);
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
      System.out.println("lookling for key: " + key);
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
      
      put("credits", ColumnHeader.CREDIT);
      put("credit", ColumnHeader.CREDIT);
      put("amount added", ColumnHeader.CREDIT);
      put("amount credited", ColumnHeader.CREDIT);
      put("added", ColumnHeader.CREDIT);
      
      put("date", ColumnHeader.DATE);
      put("date posted", ColumnHeader.DATE);
      put("posted date", ColumnHeader.DATE);
      put("dates", ColumnHeader.DATE);

      put("description", ColumnHeader.DESCRIPTION);
      put("descriptions", ColumnHeader.DESCRIPTION);
      
      put("check", ColumnHeader.DATE);
      put("check number", ColumnHeader.DATE);
      put("check#", ColumnHeader.DATE);
      put("check #", ColumnHeader.DATE);
      
      put("amount", ColumnHeader.AMOUNT);
      put("amount ($)", ColumnHeader.AMOUNT);
      put("amount($)", ColumnHeader.AMOUNT);
    }
    };
  
  
  
  
  
  
}
